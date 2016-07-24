package at.lechner.evaluation;

import java.io.IOException;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.inference.TTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.lechner.util.BasicUtil;

public class PipelineEvaluation {
	private static final Logger LOG = LoggerFactory.getLogger(PipelineEvaluation.class);
	
	public static String calcEvaluationStatistics(double[] values, boolean logging) {
		StandardDeviation mathStdDev = new StandardDeviation();
		double max = Double.NEGATIVE_INFINITY;
		double min = Double.POSITIVE_INFINITY;
		double sum = 0.0;
		for (int i = 0; i < values.length; i++) {
			if (values[i] > max)
				max = values[i];
			if (values[i] < min)
				min = values[i];
			sum += values[i];
		}
		double avg = sum / values.length;
		double stdDev = mathStdDev.evaluate(values);
		
		if(logging) {
			LOG.info(comma(max) + "\t" + comma(min) + "\t" + comma(avg) + "\t" + comma(stdDev) + "\t\n");
		}
		
		return comma(max) + "\t" + comma(min) + "\t" + comma(avg) + "\t" + comma(stdDev) + "\t";
	}
	
	public static String createPipelineAnalysis(int iterations, List<double[]> statistics) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < iterations; i++) {
			for(int j = 0; j < statistics.size(); j++) {
				sb.append(comma(statistics.get(j)[i]) + "\t");
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	private static String comma(double d) {
		return String.valueOf(d).replace(".", ",");
	}

	/**
	 * 
	 * Performs a two-sided t-test evaluating the null hypothesis that sample1
	 * and sample2 are drawn from populations with the same mean, with
	 * significance level alpha.
	 * 
	 * @param sample1Path
	 * @param sample2Path
	 * @throws IOException
	 */
	public static TTestResult tTest(String sample1Path, String sample2Path, double significanceLevel)
			throws IOException {
		String[] linesSample1 = BasicUtil.readLines(sample1Path);
		String[] linesSample2 = BasicUtil.readLines(sample2Path);

		double[] qualityTrainingSample1 = new double[linesSample1.length - 1];
		double[] performanceSample1 = new double[linesSample1.length - 1];
		double[] recallTestSample1 = new double[linesSample1.length - 1];
		double[] precisionTestSample1 = new double[linesSample1.length - 1];

		double[] qualityTrainingSample2 = new double[linesSample2.length - 1];
		double[] performanceSample2 = new double[linesSample2.length - 1];
		double[] recallTestSample2 = new double[linesSample2.length - 1];
		double[] precisionTestSample2 = new double[linesSample2.length - 1];

		for (int i = 0; i < linesSample1.length; i++) {
			if (i == 0)
				continue;
			parseLineForTTest(i - 1, linesSample1[i], qualityTrainingSample1, performanceSample1, recallTestSample1,
					precisionTestSample1);
		}

		System.out.println();

		for (int i = 0; i < linesSample2.length; i++) {
			if (i == 0)
				continue;
			parseLineForTTest(i - 1, linesSample2[i], qualityTrainingSample2, performanceSample2, recallTestSample2,
					precisionTestSample2);
		}

		TTest ttest = new TTest();
		boolean qualityTraining = ttest.tTest(qualityTrainingSample1, qualityTrainingSample2, significanceLevel);
		boolean performanceTest = ttest.tTest(performanceSample1, performanceSample2, significanceLevel);
		boolean recallTest = ttest.tTest(recallTestSample1, recallTestSample2, significanceLevel);
		boolean precisionTest = ttest.tTest(precisionTestSample1, precisionTestSample2, significanceLevel);

		PipelineEvaluation pe = new PipelineEvaluation();
		TTestResult result = pe.new TTestResult(qualityTraining, performanceTest, recallTest, precisionTest);

		return result;
	}

	private static void parseLineForTTest(int index, String line, double[] qualityTraining, double[] performance,
			double[] recallTest, double[] precisionTest) {
		String[] splitLine = line.split("\t");
		qualityTraining[index] = Double.parseDouble(splitLine[0].replace(",", "."));
		performance[index] = Double.parseDouble(splitLine[1].replace(",", "."));
		recallTest[index] = Double.parseDouble(splitLine[2].replace(",", "."));
		precisionTest[index] = Double.parseDouble(splitLine[3].replace(",", "."));
	}

	public class TTestResult {
		private boolean qualityTraining;
		private boolean performanceTest;
		private boolean recallTest;
		private boolean precisionTest;

		public TTestResult(boolean qualityTraining, boolean performanceTest, boolean recallTest,
				boolean precisionTest) {
			super();
			this.qualityTraining = qualityTraining;
			this.performanceTest = performanceTest;
			this.recallTest = recallTest;
			this.precisionTest = precisionTest;
		}

		public boolean getQualityTrainingResult() {
			return qualityTraining;
		}

		public boolean getPerformanceTestResult() {
			return performanceTest;
		}

		public boolean getRecallTestResult() {
			return recallTest;
		}

		public boolean getPrecisionTestResult() {
			return precisionTest;
		}
	}

	public static void main(String[] args) throws IOException {
		// double significanceLevel = 0.05;
		// int lowerBound = 3;
		// int upperBound = 24;
		// StringBuilder sb = new StringBuilder();
		// for (int i = lowerBound; i < upperBound + 1; i++) {
		// for (int j = lowerBound; j < upperBound + 1; j++) {
		// if (i != j) {
		// String baselinePath =
		// "evaluation/n-Fold_pipeline/pipeline_analyse_row" + i + ".tsv";
		// String testPath = "evaluation/n-Fold_pipeline/pipeline_analyse_row" +
		// j + ".tsv";
		// TTestResult ttestResult = tTest(baselinePath, testPath,
		// significanceLevel);
		// sb.append(ttestResult.getQualityTrainingResult() + "/" +
		// ttestResult.getPerformanceTestResult()
		// + "/" + ttestResult.getRecallTestResult() + "/" +
		// ttestResult.getPrecisionTestResult() + "\t");
		// } else {
		// sb.append("---\t");
		// }
		// }
		// sb.append("\n");
		// }
		// EvaluationUtil.generateCSV("evaluation/pipeline_overview.tsv",
		// sb.toString());

		TTestResult result = tTest("evaluation/n-Fold_pipeline/pipeline_analyse_row3.tsv",
				"evaluation/n-Fold_pipeline/pipeline_analyse_row30.tsv", 0.05);
		System.out.println(result.getQualityTrainingResult() + "/" + result.getPerformanceTestResult() + "/"
				+ result.getRecallTestResult() + "/" + result.getPrecisionTestResult());
	}
}
