package at.testing.weka.statistic;

import java.math.BigDecimal;
import java.math.RoundingMode;

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

	public void updateCM(int actualClass, int predictedClass) {
		cm[actualClass][predictedClass] = cm[actualClass][predictedClass] + 1;
	}

	public Double getMicroOverallRecall() {
		int truePositives = cm[0][0] + cm[1][1] + cm[2][2];
		int goldNegative = cm[0][0] + cm[0][1] + cm[0][2];
		int goldNeutral = cm[1][0] + cm[1][1] + cm[1][2];
		int goldPositive = cm[2][0] + cm[2][1] + cm[2][2];
		int totalGold = goldNegative + goldNeutral + goldPositive;
		if (totalGold == 0) {
			return -1.0;
		}
		return round((double) truePositives / totalGold);
	}

	public Double getMacroOverallRecalls() {
		return round((getRecallNegative() + getRecallNeutral() + getRecallPositive()) / 3);
	}

	public Double getMacroPosNegRecalls() {
		return round((getRecallNegative() + getRecallPositive()) / 2);
	}

	public Double getMicroOverallPrecision() {
		int truePositives = cm[0][0] + cm[1][1] + cm[2][2];
		int totalPredictedNegative = cm[0][0] + cm[1][0] + cm[2][0];
		int totalPredictedNeutral = cm[0][1] + cm[1][1] + cm[2][1];
		int totalPredictedPositive = cm[0][2] + cm[1][2] + cm[2][2];
		int totalPredicted = totalPredictedNegative + totalPredictedNeutral + totalPredictedPositive;
		if (totalPredicted == 0) {
			return -1.0;
		}
		return round((double) truePositives / totalPredicted);
	}

	public Double getMacroOverallPrecision() {
		return round((getPrecisionNegative() + getPrecisionNeutral() + getPrecisionPositive()) / 3);
	}

	public Double getMacroPosNegPrecisions() {
		return round((getPrecisionNegative() + getPrecisionPositive()) / 2);
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
		return round(numerator / denominator);
	}

	public Double getMacroPosNegFMeasure() {
//		Double numerator = getPositiveFMeasure() + getNegativeFMeasure();
//		Double denominator = 2.0;
//		return numerator / denominator;
		
		Double precision = getMacroPosNegPrecisions();
		Double recall = getMacroPosNegRecalls();
		return round(calcFMeasure(precision, recall));
	}
	
	public Double getMicroFMeasure() {
		Double precision = getMicroOverallPrecision();
		Double recall = getMicroOverallRecall();
		return round(calcFMeasure(precision, recall));
	}

	public Double getMicroPosNegFMeasure() {
		Double precision = getPrecisionForMicroPosNegFMeasure();
		Double recall = getRecallForMicroPosNegFMeasure();
		return round(calcFMeasure(precision, recall));
	}

	public Double getPrecisionForMicroPosNegFMeasure() {
		Double truePositives = (double) (cm[0][0] + cm[2][2]);
		int totalPredictedPositive = cm[0][2] + cm[1][2] + cm[2][2];
		int totalPredictedNegative = cm[0][0] + cm[1][0] + cm[2][0];
		Double allTPAndFP = (double) (totalPredictedNegative + totalPredictedPositive);
		return checkAndDivide(truePositives, allTPAndFP);
	}

	public Double getRecallForMicroPosNegFMeasure() {
		Double truePositives = (double) (cm[0][0] + cm[2][2]);
		int goldPositive = cm[2][0] + cm[2][1] + cm[2][2];
		int goldNegative = cm[0][0] + cm[0][1] + cm[0][2];
		Double allTPAndFN = (double) (goldNegative + goldPositive);
		return checkAndDivide(truePositives, allTPAndFN);
	}
	
	public Double getOtherPosNegFMeasure() {
		return round((getPositiveFMeasure() + getNegativeFMeasure()) /2);
	}
	
	//TODO
	public Double getAccuracy() {
		Double tp = (double) cm[0][0] + cm[1][1] + cm [2][2];
		Double falseStuff = (double) cm[1][0] + cm[2][0] + cm [0][1] + cm [2][1] + cm [0][2] + cm [1][2] + tp;
		return tp/falseStuff;
	}
	
	public Double getAverageAccuracy() {
		Double numerator = getNegativeAccuracy() + getNeutralAccuracy() + getPositiveAccuracy();
		Double denominator = 3.0;
		return round(numerator / denominator);
	}

	public Double getNegativeAccuracy() {
		Double tp = (double) cm[0][0];
		Double tn = (double) cm[1][1] + cm[1][2] + cm[2][1] + cm[2][2];
		Double fn = (double) cm[0][1] + cm[0][2];
		Double fp = (double) cm[1][0] + cm[2][0];
		Double numerator = tp + tn;
		Double denominator = tp + fn + fp + tn;
		return numerator / denominator;
	}

	public Double getNeutralAccuracy() {
		Double tp = (double) cm[1][1];
		Double tn = (double) cm[0][0] + cm[0][2] + cm[2][0] + cm[2][2];
		Double fn = (double) cm[1][0] + cm[1][2];
		Double fp = (double) cm[0][1] + cm[2][1];
		Double numerator = tp + tn;
		Double denominator = tp + fn + fp + tn;
		return numerator / denominator;
	}

	public Double getPositiveAccuracy() {
		Double tp = (double) cm[2][2];
		Double tn = (double) cm[0][0] + cm[0][1] + cm[1][0] + cm[1][1];
		Double fn = (double) cm[2][0] + cm[2][1];
		Double fp = (double) cm[0][2] + cm[1][2];
		Double numerator = tp + tn;
		Double denominator = tp + fn + fp + tn;
		return numerator / denominator;
	}

	public Double getErrorRate() {
		Double numerator = getNegativeErrorRate() + getNeutralErrorRate() + getPositiveErrorRate();
		Double denominator = 3.0;
		return round(numerator / denominator);
	}

	public Double getNegativeErrorRate() {
		Double tp = (double) cm[0][0];
		Double tn = (double) cm[1][1] + cm[1][2] + cm[2][1] + cm[2][2];
		Double fn = (double) cm[0][1] + cm[0][2];
		Double fp = (double) cm[1][0] + cm[2][0];
		Double numerator = fp + fn;
		Double denominator = tp + fn + fp + tn;
		return numerator / denominator;
	}

	public Double getNeutralErrorRate() {
		Double tp = (double) cm[1][1];
		Double tn = (double) cm[0][0] + cm[0][2] + cm[2][0] + cm[2][2];
		Double fn = (double) cm[1][0] + cm[1][2];
		Double fp = (double) cm[0][1] + cm[2][1];
		Double numerator = tp + tn;
		Double denominator = tp + fn + fp + tn;
		return numerator / denominator;
	}

	public Double getPositiveErrorRate() {
		Double tp = (double) cm[2][2];
		Double tn = (double) cm[0][0] + cm[0][1] + cm[1][0] + cm[1][1];
		Double fn = (double) cm[2][0] + cm[2][1];
		Double fp = (double) cm[0][2] + cm[1][2];
		Double numerator = tp + tn;
		Double denominator = tp + fn + fp + tn;
		return numerator / denominator;
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
	
	public static double round(double value) {
	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(2, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}

}
