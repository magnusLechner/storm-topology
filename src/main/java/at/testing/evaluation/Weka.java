package at.testing.evaluation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import at.testing.util.EvaluationUtil;

public class Weka {

	private List<String> files = new ArrayList<String>();

	public void evaluateScriptResults(String folderPath) {
		File folder = new File(folderPath);
		try {
			searchForTextFilesInFolder(folder);
			Collections.sort(files);
			WekaStatistics wekaStatistics = new WekaStatistics(files.size());
			for (int i = 0; i < files.size(); i++) {
				extractResult(folderPath + File.separator + files.get(i), wekaStatistics, i);
			}
			String overView = createOverview(wekaStatistics, files);
			EvaluationUtil.generateTSV(folderPath + File.separator + ".." + File.separator + "overview.tsv", overView);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void searchForTextFilesInFolder(File folder) throws IOException {
		for (File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				searchForTextFilesInFolder(fileEntry);
			} else {
				if (fileEntry.getName().endsWith(".txt")) {
					files.add(fileEntry.getName());
				}
			}
		}
	}

	private void extractResult(String filePath, WekaStatistics wekaStatistics, int index) {
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			String line;
			boolean lookForClassResults = true;
			boolean isTestResults = false;
			int correctClassified = 0;
			int incorrectClassified = 0;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.length() == 0) {
					continue;
				}
				double[] info = extractLineInfo(line);
				if(line.contains("Time taken to test model on training data")) {
					wekaStatistics.addTime(index, info[0]);
				}
				if(line.contains("=== Error on test data ===")) {
					isTestResults = true;
				}
				if(isTestResults) {
					if (line.contains("Correctly Classified Instances")) {
						correctClassified = (int) info[0];
						wekaStatistics.addOverallPrecision(index, info[1]);
					}
					if (line.contains("Incorrectly Classified Instances")) {
						incorrectClassified = (int) info[0];
						;
					}
					if (line.contains("Weighted Avg")) {
						lookForClassResults = false;
					}
					if (line.contains("Total Number of Instances")) {
						int total = (int) info[0];
						wekaStatistics.addOverallRecall(index, (correctClassified + incorrectClassified) / total);
					}
					if (lookForClassResults) {
						if (line.contains("POSITIVE")) {
							wekaStatistics.addPositivePrecision(index, info[2]);
							wekaStatistics.addPositiveRecall(index, info[3]);
						}
						if (line.contains("NEUTRAL")) {
							wekaStatistics.addNeutralPrecision(index, info[2]);
							wekaStatistics.addNeutralRecall(index, info[3]);
						}
						if (line.contains("NEGATIVE")) {
							wekaStatistics.addNegativePrecision(index, info[2]);
							wekaStatistics.addNegativeRecall(index, info[3]);
						}
					}	
				}
			}
			wekaStatistics.addMacroAvgPrecision(index, (wekaStatistics.getPositivePrecision()[index]
					+ wekaStatistics.getNeutralPrecision()[index] + wekaStatistics.getNegativePrecision()[index]) / 3);
			wekaStatistics.addMacroAvgRecall(index, (wekaStatistics.getPositiveRecall()[index]
					+ wekaStatistics.getNeutralRecall()[index] + wekaStatistics.getNegativeRecall()[index]) / 3);
			wekaStatistics.addMacroAvgPosNegPrecision(index,
					(wekaStatistics.getPositivePrecision()[index] + wekaStatistics.getNegativePrecision()[index]) / 2);
			wekaStatistics.addMacroAvgPosNegRecall(index,
					(wekaStatistics.getPositiveRecall()[index] + wekaStatistics.getNegativeRecall()[index]) / 2);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private double[] extractLineInfo(String line) {
		List<Double> infoList = new ArrayList<Double>();
		String[] splitLine = line.split(" ");
		String regExp = "[\\x00-\\x20]*[+-]?(((((\\p{Digit}+)(\\.)?((\\p{Digit}+)?)([eE][+-]?(\\p{Digit}+))?)|(\\.((\\p{Digit}+))([eE][+-]?(\\p{Digit}+))?)|(((0[xX](\\p{XDigit}+)(\\.)?)|(0[xX](\\p{XDigit}+)?(\\.)(\\p{XDigit}+)))[pP][+-]?(\\p{Digit}+)))[fFdD]?))[\\x00-\\x20]*";
		for (String part : splitLine) {
			part = part.replace(",", ".");
			if (!part.equals("") && part.matches(regExp)) {
				infoList.add(Double.parseDouble(part));
			}
		}
		double[] info = new double[infoList.size()];
		for (int i = 0; i < infoList.size(); i++) {
			info[i] = infoList.get(i);
		}
		return info;
	}
	
	private String createOverview(WekaStatistics wekaStatistics, List<String> fileNames) {
		StringBuilder sb = new StringBuilder();
		String lastClassifier = "";
		List<String> classifier = new ArrayList<String>();
		List<Integer> lengths = new ArrayList<Integer>();
		List<Integer> addedLengths = new ArrayList<Integer>();
		int length = 1;
		for(int i = 0; i < fileNames.size(); i++) {
			String currentClassifier = fileNames.get(i).split("_")[0];
			if(!currentClassifier.equals(lastClassifier)) {
				classifier.add(currentClassifier);
				if(!lastClassifier.equals("")) {
					lengths.add(length);	
					length = 1;
				}
			} else {
				length++;
			}
			lastClassifier = currentClassifier;
		}
		lengths.add(length);
		
		for(int i = 0; i < lengths.size(); i++) {
			if(i > 0) {
				addedLengths.add(addedLengths.get(i-1) + lengths.get(i));	
			} else {
				addedLengths.add(lengths.get(i));
			}
		}
		
		double maxOverallPrecision = 0.0;
		double maxMacroAvgPrec = 0.0;
		double maxMacroAvgPosNegPrec = 0.0;
		double maxMacroAvgRecall = 0.0;
		double maxMacroAvgPosNegRecall = 0.0;
		
		for(int i = 0; i < classifier.size(); i++) {
			String overallPrecision = "";
			String macroAvgPrecision = "";
			String macroAvgPosNegPrecision = "";
			String overallRecall = "";
			String macroAvgRecall = "";
			String macroAvgPosNegRecall = "";

			String positivePrecision = "";
			String positiveRecall = "";
			String neutralPrecision = "";
			String neutralRecall = "";
			String negativePrecision = "";
			String negativeRecall = "";
			
			String time = "";
			
			int start;
			if(i == 0) {
				start = 0;
			} else {
				start = addedLengths.get(i) - lengths.get(i);
			}		
			
			for(int j = start; j < addedLengths.get(i); j++) {
				
				if(wekaStatistics.getOverallPrecision()[j] > maxOverallPrecision) {
					maxOverallPrecision = wekaStatistics.getOverallPrecision()[j];
				}
				if(wekaStatistics.getMacroAvgPrecision()[j] > maxMacroAvgPrec) {
					maxMacroAvgPrec = wekaStatistics.getMacroAvgPrecision()[j];
				}
				if(wekaStatistics.getMacroAvgPosNegPrecision()[j] > maxMacroAvgPosNegPrec) {
					maxMacroAvgPosNegPrec = wekaStatistics.getMacroAvgPosNegPrecision()[j];
				}
				if(wekaStatistics.getMacroAvgRecall()[j] > maxMacroAvgRecall) {
					maxMacroAvgRecall = wekaStatistics.getMacroAvgRecall()[j];
				}
				if(wekaStatistics.getMacroAvgPosNegRecall()[j] > maxMacroAvgPosNegRecall) {
					maxMacroAvgPosNegRecall = wekaStatistics.getMacroAvgPosNegRecall()[j];
				}
				
				
				overallPrecision += String.valueOf((wekaStatistics.getOverallPrecision()[j])/100).replaceAll("(\\d+)\\.(\\d+)", "$1,$2") + "\t"; 
				macroAvgPrecision += String.valueOf(wekaStatistics.getMacroAvgPrecision()[j]).replaceAll("(\\d+)\\.(\\d+)", "$1,$2") + "\t";
				macroAvgPosNegPrecision += String.valueOf(wekaStatistics.getMacroAvgPosNegPrecision()[j]).replaceAll("(\\d+)\\.(\\d+)", "$1,$2") + "\t";
				overallRecall += String.valueOf(wekaStatistics.getOverallRecall()[j]).replaceAll("(\\d+)\\.(\\d+)", "$1,$2") + "\t";
				macroAvgRecall += String.valueOf(wekaStatistics.getMacroAvgRecall()[j]).replaceAll("(\\d+)\\.(\\d+)", "$1,$2") + "\t";
				macroAvgPosNegRecall += String.valueOf(wekaStatistics.getMacroAvgPosNegRecall()[j]).replaceAll("(\\d+)\\.(\\d+)", "$1,$2") + "\t";

				positivePrecision += String.valueOf(wekaStatistics.getPositivePrecision()[j]).replaceAll("(\\d+)\\.(\\d+)", "$1,$2") + "\t";
				positiveRecall += String.valueOf(wekaStatistics.getPositiveRecall()[j]).replaceAll("(\\d+)\\.(\\d+)", "$1,$2") + "\t";
				neutralPrecision += String.valueOf(wekaStatistics.getNeutralPrecision()[j]).replaceAll("(\\d+)\\.(\\d+)", "$1,$2") + "\t";
				neutralRecall += String.valueOf(wekaStatistics.getNeutralRecall()[j]).replaceAll("(\\d+)\\.(\\d+)", "$1,$2") + "\t";
				negativePrecision += String.valueOf(wekaStatistics.getNegativePrecision()[j]).replaceAll("(\\d+)\\.(\\d+)", "$1,$2") + "\t";
				negativeRecall += String.valueOf(wekaStatistics.getNegativeRecall()[j]).replaceAll("(\\d+)\\.(\\d+)", "$1,$2") + "\t";
				
				time += String.valueOf(wekaStatistics.getTime()[j]).replaceAll("(\\d+)\\.(\\d+)", "$1,$2") + "\t";
			}
			sb.append(classifier.get(i) + "\n");
			sb.append(overallPrecision + "\n");
			sb.append(macroAvgPrecision + "\n");
			sb.append(macroAvgPosNegPrecision + "\n");
			sb.append(overallRecall + "\n");
			sb.append(macroAvgRecall + "\n");
			sb.append(macroAvgPosNegRecall + "\n");
			sb.append(positivePrecision + "\n");
			sb.append(positiveRecall + "\n");
			sb.append(neutralPrecision + "\n");
			sb.append(neutralRecall + "\n");
			sb.append(negativePrecision + "\n");
			sb.append(negativeRecall + "\n");
			sb.append(time + "\n");
		}
		
		System.out.println("Max OverallPrecision: " + String.valueOf(maxOverallPrecision).replaceAll("(\\d+)\\.(\\d+)", "$1,$2"));
		System.out.println("Max MacroAvgPrecision: " + String.valueOf(maxMacroAvgPrec).replaceAll("(\\d+)\\.(\\d+)", "$1,$2"));
		System.out.println("Max MacroAvgPosNegPrecision: " + String.valueOf(maxMacroAvgPosNegPrec).replaceAll("(\\d+)\\.(\\d+)", "$1,$2"));
		System.out.println("Max MacroAvgRecall: " + String.valueOf(maxMacroAvgRecall).replaceAll("(\\d+)\\.(\\d+)", "$1,$2"));
		System.out.println("Max MacroAvgPosNegRecall: " + String.valueOf(maxMacroAvgPosNegRecall).replaceAll("(\\d+)\\.(\\d+)", "$1,$2"));
		
		return sb.toString();
	}

	public static void main(String[] args) {
		String scriptResultsPath = "/home/magnus/workspace/storm-topology/src/main/evaluation/weka_script/script_results";

		Weka weka = new Weka();
		weka.evaluateScriptResults(scriptResultsPath);
	}

	public class WekaStatistics {
		private double[] overallPrecision;
		private double[] macroAvgPrecision;
		private double[] macroAvgPosNegPrecision;
		private double[] overallRecall;
		private double[] macroAvgRecall;
		private double[] macroAvgPosNegRecall;

		private double[] positivePrecision;
		private double[] positiveRecall;
		private double[] neutralPrecision;
		private double[] neutralRecall;
		private double[] negativePrecision;
		private double[] negativeRecall;
		
		private double[] time;

		public WekaStatistics(int length) {
			this.overallPrecision = new double[length];
			this.macroAvgPrecision = new double[length];
			this.macroAvgPosNegPrecision = new double[length];
			this.overallRecall = new double[length];
			this.macroAvgRecall = new double[length];
			this.macroAvgPosNegRecall = new double[length];

			this.positivePrecision = new double[length];
			this.positiveRecall = new double[length];
			this.neutralPrecision = new double[length];
			this.neutralRecall = new double[length];
			this.negativePrecision = new double[length];
			this.negativeRecall = new double[length];
			
			this.time = new double[length];
		}
		
		public int getNumberStatistics() {
			return 13;
		}

		public void addOverallPrecision(int index, double value) {
			overallPrecision[index] = value;
		}

		public void addMacroAvgPrecision(int index, double value) {
			macroAvgPrecision[index] = value;
		}

		public void addMacroAvgPosNegPrecision(int index, double value) {
			macroAvgPosNegPrecision[index] = value;
		}

		public void addOverallRecall(int index, double value) {
			overallRecall[index] = value;
		}

		public void addMacroAvgRecall(int index, double value) {
			macroAvgRecall[index] = value;
		}

		public void addMacroAvgPosNegRecall(int index, double value) {
			macroAvgPosNegRecall[index] = value;
		}

		public void addPositivePrecision(int index, double value) {
			positivePrecision[index] = value;
		}

		public void addPositiveRecall(int index, double value) {
			positiveRecall[index] = value;
		}

		public void addNeutralPrecision(int index, double value) {
			neutralPrecision[index] = value;
		}

		public void addNeutralRecall(int index, double value) {
			neutralRecall[index] = value;
		}

		public void addNegativePrecision(int index, double value) {
			negativePrecision[index] = value;
		}

		public void addNegativeRecall(int index, double value) {
			negativeRecall[index] = value;
		}

		public void addTime(int index, double value) {
			time[index] = value;
		}
		
		public double[] getOverallPrecision() {
			return overallPrecision;
		}

		public double[] getMacroAvgPrecision() {
			return macroAvgPrecision;
		}

		public double[] getMacroAvgPosNegPrecision() {
			return macroAvgPosNegPrecision;
		}

		public double[] getOverallRecall() {
			return overallRecall;
		}

		public double[] getMacroAvgRecall() {
			return macroAvgRecall;
		}

		public double[] getMacroAvgPosNegRecall() {
			return macroAvgPosNegRecall;
		}

		public double[] getPositivePrecision() {
			return positivePrecision;
		}

		public double[] getPositiveRecall() {
			return positiveRecall;
		}

		public double[] getNeutralPrecision() {
			return neutralPrecision;
		}

		public double[] getNeutralRecall() {
			return neutralRecall;
		}

		public double[] getNegativePrecision() {
			return negativePrecision;
		}

		public double[] getNegativeRecall() {
			return negativeRecall;
		}

		public double[] getTime() {
			return time;
		}
		
	}
}
