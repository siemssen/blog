package blog.common.numerical;

import java.util.Arrays;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import blog.common.Util;

/**
 * Matrix and linear algebra operations
 * 
 * @author awong
 * @author leili
 * @date Feb 11, 2014
 */
public class JamaMatrixLib implements MatrixLib {

  private Matrix values;

  public JamaMatrixLib(double[][] contents) {
    values = new Matrix(contents);
  }

  public JamaMatrixLib(Matrix contents) {
    values = contents;
  }

  @Override
  public double elementAt(int i, int j) {
    return values.get(i, j);
  }

  @Override
  public void setElement(int x, int y, double val) {
    values.set(x, y, val);
  }

  @Override
  public String toString() {
    return Arrays.deepToString(values.getArray());
  }

  @Override
  public int numRows() {
    return values.getRowDimension();
  }

  @Override
  public int numCols() {
    return values.getColumnDimension();
  }

  @Override
  public MatrixLib sliceRow(int i) {
    return new JamaMatrixLib(values.getMatrix(i, i, 0,
        values.getColumnDimension() - 1));
  }

  @Override
  public MatrixLib sliceRows(int i, int j) {
    return new JamaMatrixLib(values.getMatrix(i, j, 0,
        values.getColumnDimension() - 1));
  }

  @Override
  public MatrixLib sliceCol(int i) {
    return new JamaMatrixLib(values.getMatrix(0, values.getRowDimension() - 1,
        i, i));
  }

  @Override
  public MatrixLib sliceCols(int i, int j) {
    return new JamaMatrixLib(values.getMatrix(0, values.getRowDimension() - 1,
        i, j));
  }

  @Override
  public MatrixLib subMat(int x1, int y1, int x2, int y2) {
    return new JamaMatrixLib(values.getMatrix(x1, x2, y1, y2));
  }

  @Override
  public MatrixLib plus(MatrixLib otherMat) {
    if (otherMat instanceof JamaMatrixLib) {
      JamaMatrixLib newMat = (JamaMatrixLib) otherMat;
      return new JamaMatrixLib(values.plus(newMat.values));
    }
    throw new ClassCastException(
        "Only one matrix library should be in use at a time!");
  }

  @Override
  public MatrixLib minus(MatrixLib otherMat) {
    if (otherMat instanceof JamaMatrixLib) {
      JamaMatrixLib newMat = (JamaMatrixLib) otherMat;
      return new JamaMatrixLib(values.minus(newMat.values));
    }
    throw new ClassCastException(
        "Only one matrix library should be in use at a time!");
  }

  @Override
  public MatrixLib timesScale(double scale) {
    return new JamaMatrixLib(values.times(scale));
  }

  @Override
  public MatrixLib timesMat(MatrixLib otherMat) {
    if (otherMat instanceof JamaMatrixLib) {
      JamaMatrixLib newMat = (JamaMatrixLib) otherMat;
      return new JamaMatrixLib(values.times(newMat.values));
    }
    throw new ClassCastException(
        "Only one matrix library should be in use at a time!");
  }

  @Override
  public double det() {
    return values.det();
  }

  @Override
  public double trace() {
    return values.trace();
  }

  @Override
  public double logDet() {
    double logDet = 0.0;
    for (double val : eigenvals()) {
      logDet += Math.log(val);
    }
    return logDet;
  }

  @Override
  public MatrixLib transpose() {
    return new JamaMatrixLib(values.transpose());
  }

  @Override
  public MatrixLib repmat(int rowTimes, int colTimes) {
    if (rowTimes <= 0 || colTimes <= 0) {
      throw new IllegalArgumentException(
          "The number of blocks specified for repmat in each dimension must be strictly positive");
    }
    double[][] ary = values.getArrayCopy();
    double[][] newAry = new double[rowTimes * ary.length][colTimes
        * ary[0].length];
    for (int i = 0; i < rowTimes; i++) {
      for (int j = 0; j < colTimes; j++) {
        for (int k = 0; k < ary.length; k++) {
          System.arraycopy(ary[k], 0, newAry[i * ary.length + k], j
              * ary[0].length, ary[0].length);
        }
      }
    }
    return MatrixFactory.fromArray(newAry);
  }

  @Override
  public MatrixLib inverse() {
    return new JamaMatrixLib(values.inverse());
  }

  @Override
  public MatrixLib choleskyFactor() {
    return new JamaMatrixLib(values.chol().getL());
  }

  @Override
  public MatrixLib columnSum() {
    double[][] result = new double[1][numCols()];
    for (int i = 0; i < numCols(); i++) {
      result[0][i] = 0;
    }
    for (int j = 0; j < numRows(); j++) {
      for (int i = 0; i < numCols(); ++i)
        result[0][i] += elementAt(j, i);
    }
    return new JamaMatrixLib(result);
  }

  @Override
  public MatrixLib rowSum() {
    double[][] result = new double[numRows()][1];
    for (int i = 0; i < numRows(); i++) {
      result[i][0] = 0;
      for (int j = 0; j < numCols(); j++) {
        result[i][0] += elementAt(i, j);
      }
    }
    return new JamaMatrixLib(result);
  }

  @Override
  public double matSum() {
    double result = 0;
    for (int i = 0; i < numRows(); i++) {
      for (int j = 0; j < numCols(); j++) {
        result += elementAt(i, j);
      }
    }
    return result;
  }

  @Override
  public double[] eigenvals() {
    EigenvalueDecomposition decomp = new EigenvalueDecomposition(values);
    return decomp.getRealEigenvalues();
  }

  @Override
  public boolean equals(Object obj) {
    // in general you need to check the dimensionality of the matrix as well.
    if (obj instanceof JamaMatrixLib) {
      return Arrays.deepEquals(values.getArray(),
          ((JamaMatrixLib) obj).values.getArray());
    }
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return Arrays.deepHashCode(values.getArray());
  }

  /*
   * (non-Javadoc)
   * 
   * @see blog.common.numerical.MatrixLib#exp()
   */
  @Override
  public MatrixLib exp() {
    double[][] v = values.getArrayCopy();
    for (int i = 0; i < numRows(); i++) {
      for (int j = 0; j < numCols(); j++)
        v[i][j] = Math.exp(v[i][j]);
    }
    return new JamaMatrixLib(v);
  }

  /*
   * (non-Javadoc)
   * 
   * @see blog.common.numerical.MatrixLib#log()
   */
  @Override
  public MatrixLib log() {
    double[][] v = values.getArrayCopy();
    for (int i = 0; i < numRows(); i++) {
      for (int j = 0; j < numCols(); j++)
        v[i][j] = Math.log(v[i][j]);
    }
    return new JamaMatrixLib(v);
  }

  /*
   * (non-Javadoc)
   * 
   * @see blog.common.numerical.MatrixLib#abs()
   */
  @Override
  public MatrixLib abs() {
    double[][] v = values.getArrayCopy();
    for (int i = 0; i < numRows(); i++) {
      for (int j = 0; j < numCols(); j++)
        v[i][j] = Math.abs(v[i][j]);
    }
    return new JamaMatrixLib(v);
  }

  /*
   * (non-Javadoc)
   * 
   * @see blog.common.numerical.MatrixLib#isSymmetric()
   */
  @Override
  public boolean isSymmetric() {
    if (numRows() == numCols()) {
      int size = numRows();
      for (int i = 0; i < size; i++) {
        for (int j = i + 1; j < size; j++) {
          double val = elementAt(i, j) - elementAt(j, i);
          if (!Util.closeToZero(val)) {
            return false;
          }
        }
      }
    }
    return true;
  }

}
