package at.lechner.weka.statistic;

public class ConfusionMatrixStatistic {

	private int[][] cm;

	public ConfusionMatrixStatistic() {
		this.cm = new int[3][3];
	}

	public ConfusionMatrixStatistic(int[][] cm) {
		this.cm = cm;
	}

	public int[][] getCM() {
		return cm;
	}

	public void updateCM(int predictedClass, int actualClass) {
		cm[predictedClass][actualClass] = cm[predictedClass][actualClass] + 1;
	}
	
	public Double getOverallRecall() {
		int truePositives = cm[0][0] + cm[1][1] + cm[2][2];
		int goldNegative = cm[0][0] + cm[0][1] + cm[0][2];
		int goldNeutral = cm[1][0] + cm[1][1] + cm[1][2];
		int goldPositive = cm[2][0] + cm[2][1] + cm[2][2];
		int totalGold = goldNegative + goldNeutral + goldPositive;
		if (totalGold == 0) {
			return -1.0;
		}
		return (double) truePositives / totalGold;
	}

	public Double getMacroOverallRecalls() {
		return (getRecallNegative() + getRecallNeutral() + getRecallPositive()) / 3;
	}

	public Double getMacroPosNegRecalls() {
		return (getRecallNegative() + getRecallPositive()) / 2;
	}

	public Double getOverallPrecision() {
		int truePositives = cm[0][0] + cm[1][1] + cm[2][2];
		int totalPredictedNegative = cm[0][0] + cm[1][0] + cm[2][0];
		int totalPredictedNeutral = cm[0][1] + cm[1][1] + cm[2][1];
		int totalPredictedPositive = cm[0][2] + cm[1][2] + cm[2][2];
		int totalPredicted = totalPredictedNegative + totalPredictedNeutral + totalPredictedPositive;
		if (totalPredicted == 0) {
			return -1.0;
		}
		return (double) truePositives / totalPredicted;
	}

	public Double getMacroOverallPrecision() {
		return (getPrecisionNegative() + getPrecisionNeutral() + getPrecisionPositive()) / 3;
	}

	public Double getMacroPosNegPrecisions() {
		return (getPrecisionNegative() + getPrecisionPositive()) / 2;
	}

	public Double getRecallPositive() {
		int tpPositive = cm[2][2];
		int goldPositive = cm[2][0] + cm[2][1] + cm[2][2];
		if (goldPositive == 0) {
			return -1.0;
		}
		return (double) tpPositive / goldPositive;
	}

	public Double getPrecisionPositive() {
		int tpPositive = cm[2][2];
		int totalPredictedPositive = cm[0][2] + cm[1][2] + cm[2][2];
		if (totalPredictedPositive == 0) {
			return -1.0;
		}
		return (double) tpPositive / totalPredictedPositive;
	}

	public Double getRecallNeutral() {
		int tpNeutral = cm[1][1];
		int goldNeutral = cm[1][0] + cm[1][1] + cm[1][2];
		if (goldNeutral == 0) {
			return -1.0;
		}
		return (double) tpNeutral / goldNeutral;
	}

	public Double getPrecisionNeutral() {
		int tpNeutral = cm[1][1];
		int totalPredictedNeutral = cm[0][1] + cm[1][1] + cm[2][1];
		if (totalPredictedNeutral == 0) {
			return -1.0;
		}
		return (double) tpNeutral / totalPredictedNeutral;
	}

	public Double getRecallNegative() {
		int tpNegative = cm[0][0];
		int goldNegative = cm[0][0] + cm[0][1] + cm[0][2];
		if (goldNegative == 0) {
			return -1.0;
		}
		return (double) tpNegative / goldNegative;
	}

	public Double getPrecisionNegative() {
		int tpNegative = cm[0][0];
		int totalPredictedNegative = cm[0][0] + cm[1][0] + cm[2][0];
		if (totalPredictedNegative == 0) {
			return -1.0;
		}
		return (double) tpNegative / totalPredictedNegative;
	}

	public Double getPositiveFMeasure() {
		Double recall = getRecallPositive();
		Double precision = getPrecisionPositive();
		return calcFMeasure(precision, recall);
	}

	public Double getNeutralFMeasure() {
		Double recall = getRecallNeutral();
		Double precision = getPrecisionNeutral();
		return calcFMeasure(precision, recall);
	}

	public Double getNegativeFMeasure() {
		Double recall = getRecallNegative();
		Double precision = getPrecisionNegative();
		return calcFMeasure(precision, recall);
	}

	public Double getMacroFMeasure() {
		Double numerator = getPositiveFMeasure() + getNeutralFMeasure() + getNegativeFMeasure();
		Double denominator = 3.0;
		return numerator / denominator;
	}

	public Double getMacroPosNegFMeasure() {
		Double numerator = getPositiveFMeasure() + getNegativeFMeasure();
		Double denominator = 2.0;
		return numerator / denominator;
	}

	public Double getMicroFMeasure() {
		Double precision = getPrecisionForMicroFMeasure();
		Double recall = getRecallForMicroFMeasure();
		return calcFMeasure(precision, recall);
	}

	public Double getPrecisionForMicroFMeasure() {
		Double truePositives = (double) (cm[0][0] + cm[1][1] + cm[2][2]);
		int totalPredictedPositive = cm[0][2] + cm[1][2] + cm[2][2];
		int totalPredictedNeutral = cm[0][1] + cm[1][1] + cm[2][1];
		int totalPredictedNegative = cm[0][0] + cm[1][0] + cm[2][0];
		Double allTPAndFP = (double) (totalPredictedNegative + totalPredictedNeutral + totalPredictedPositive);
		return checkAndDivide(truePositives, allTPAndFP);
	}

	public Double getRecallForMicroFMeasure() {
		Double truePositives = (double) (cm[0][0] + cm[1][1] + cm[2][2]);
		int goldPositive = cm[2][0] + cm[2][1] + cm[2][2];
		int goldNeutral = cm[1][0] + cm[1][1] + cm[1][2];
		int goldNegative = cm[0][0] + cm[0][1] + cm[0][2];
		Double allTPAndFN = (double) (goldNegative + goldNeutral + goldPositive);
		return checkAndDivide(truePositives, allTPAndFN);
	}

	public Double getMicroPosNegFMeasure() {
		Double precision = getPrecisionForMicroPosNegFMeasure();
		Double recall = getRecallForMicroPosNegFMeasure();
		return calcFMeasure(precision, recall);
	}

	public Double getPrecisionForMicroPosNegFMeasure() {
		Double truePositives = (double) (cm[0][0] + cm[2][2]);
		int totalPredictedPositive = cm[0][2] + cm[2][2];
		int totalPredictedNegative = cm[0][0] + cm[2][0];
		Double allTPAndFP = (double) (totalPredictedNegative + totalPredictedPositive);
		return checkAndDivide(truePositives, allTPAndFP);
	}

	public Double getRecallForMicroPosNegFMeasure() {
		Double truePositives = (double) (cm[0][0] + cm[2][2]);
		int goldPositive = cm[2][0] + cm[2][2];
		int goldNegative = cm[0][0] + cm[0][2];
		Double allTPAndFN = (double) (goldNegative + goldPositive);
		return checkAndDivide(truePositives, allTPAndFN);
	}

	private Double calcFMeasure(Double precision, Double recall) {
		if (precision == 0 && recall == 0) {
			return -1.0;
		}
		return (2 * precision * recall) / (precision + recall);
	}

	private Double checkAndDivide(Double numerator, Double denominator) {
		if (denominator == 0) {
			return -1.0;
		}
		return numerator / denominator;
	}

}
