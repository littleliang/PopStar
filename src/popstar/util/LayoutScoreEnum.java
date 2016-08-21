package popstar.util;

public enum LayoutScoreEnum {
	singledot_too_many(-2), singledot_fault(-1), max_connect(Double.MAX_VALUE);
	
	private double score;
	
	private LayoutScoreEnum(double score) {
		this.score = score;
	}

	public double getScore() {
		return score;
	}
	
}
