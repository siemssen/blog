package blog.absyn;

/**
 * @author leili
 * @date Apr 22, 2012
 */
public class ValueEvidence extends EvidenceStmt {
	public Expr left;
	public Expr right;

	public ValueEvidence(int p, Expr s, Expr t) {
		pos = p;
		left = s;
		right = t;
	}

	@Override
	public void printTree(Printer pr, int d) {
		pr.indent(d);
		pr.sayln("ValueEvidence(");
		if (left != null) {
			left.printTree(pr, d + 1);
		}
		if (right != null) {
			pr.sayln(",");
			right.printTree(pr, d + 1);
		}
		pr.say(")");
	}

}
