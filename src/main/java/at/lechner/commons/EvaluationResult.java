package at.lechner.commons;

public class EvaluationResult {

	private int correctNeutral = 0;
	private int correctPositive = 0;
	private int correctNegative = 0;

	private int positiveFalseNeutral = 0;
	private int positiveFalseNegative = 0;

	private int negativeFalsePositive = 0;
	private int negativeFalseNeutral = 0;

	private int neutralFalsePositive = 0;
	private int neutralFalseNegative = 0;

	private int mixedFalsePositive = 0;
	private int mixedFalseNegative = 0;
	private int mixedFalseNeutral = 0;

	public void updateCorrectNeutral() {
		correctNeutral += 1;
	}

	public void updateCorrectPositive() {
		correctPositive += 1;
	}

	public void updateCorrectNegative() {
		correctNegative += 1;
	}

	public int getCorrectSum() {
		return correctNegative + correctNeutral + correctPositive;
	}

	public void updatePositiveFalseNegative() {
		positiveFalseNegative += 1;
	}

	public void updatePositiveFalseNeutral() {
		positiveFalseNeutral += 1;
	}

	public void updateNegativeFalsePositive() {
		negativeFalsePositive += 1;
	}

	public void updateNegativeFalseNeutral() {
		negativeFalseNeutral += 1;
	}

	public void updateNeutralFalsePositive() {
		neutralFalsePositive += 1;
	}

	public void updateNeutralFalseNegative() {
		neutralFalseNegative += 1;
	}

	public void updateMixedFalseNegative() {
		mixedFalseNegative += 1;
	}

	public void updateMixedFalseNeutral() {
		mixedFalseNeutral += 1;
	}

	public void updateMixedFalsePositive() {
		mixedFalsePositive += 1;
	}

	public int getFalseSum() {
		return positiveFalseNegative + positiveFalseNeutral + negativeFalseNeutral + negativeFalsePositive
				+ neutralFalseNegative + neutralFalsePositive + mixedFalseNegative + mixedFalseNeutral
				+ mixedFalsePositive;
	}

	public int getCorrectNeutral() {
		return correctNeutral;
	}

	public int getCorrectPositive() {
		return correctPositive;
	}

	public int getCorrectNegative() {
		return correctNegative;
	}

	public int getPositiveFalseNeutral() {
		return positiveFalseNeutral;
	}

	public int getPositiveFalseNegative() {
		return positiveFalseNegative;
	}

	public int getNegativeFalsePositive() {
		return negativeFalsePositive;
	}

	public int getNegativeFalseNeutral() {
		return negativeFalseNeutral;
	}

	public int getNeutralFalsePositive() {
		return neutralFalsePositive;
	}

	public int getNeutralFalseNegative() {
		return neutralFalseNegative;
	}

	public int getMixedFalsePositive() {
		return mixedFalsePositive;
	}

	public int getMixedFalseNegative() {
		return mixedFalseNegative;
	}

	public int getMixedFalseNeutral() {
		return mixedFalseNeutral;
	}

}
