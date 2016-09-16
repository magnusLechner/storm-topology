package at.lechner.weka.classifier;

import java.util.ArrayList;
import java.util.List;

import at.lechner.weka.WekaEvaluator;
import at.lechner.weka.option.MyOption;
import at.lechner.weka.option.MyPARTOption;
import weka.classifiers.rules.PART;
import weka.core.Instances;
import weka.core.Utils;

public class MyPART extends MyClassifier {

	private PART part;

	public MyPART(PART part) {
		this(part, "PART");
	}

	public MyPART(PART part, String name) {
		super(part, name);
		this.part = part;
		addTestOptions();
	}

	public void addTestOptions() {
		try {

			// TODO

//			String[] options1 = Utils.splitOptions("-F 1 -M 1.5");
//			
//			addOption(options1);

			if (getOptionsList().size() == 0) {
				System.err.println(getName() + ": No test options!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setCurrentOption(String[] options) throws Exception {
		part.setOptions(options);
	}

	@Override
	public String getCompleteCurrentOption() {
		String[] option = part.getOptions();
		String res = "";
		for (String s : option) {
			res += s + " ";
		}
		return res;
	}

	@Override
	public List<MyOption> defineOptionsForOptimization(Instances trainingsData) throws Exception {
		List<MyOption> options = new ArrayList<MyOption>();

		MyPARTOption option1 = new MyPARTOption(new PART(), trainingsData);
		option1.addCVParameter("C 0.1 1.0 10");
		option1.addCVParameter("N 2 8 4");

		MyPARTOption option2 = new MyPARTOption(new PART(), trainingsData);
		option2.setOptions("-doNotMakeSplitPointActualValue");
		option2.addCVParameter("C 0.1 1.0 10");
		option2.addCVParameter("N 2 8 4");

		MyPARTOption option3 = new MyPARTOption(new PART(), trainingsData);
		option3.setOptions("-R");
		option3.addCVParameter("C 0.1 1.0 10");
		option3.addCVParameter("N 2 8 4");

		MyPARTOption option4 = new MyPARTOption(new PART(), trainingsData);
		option2.setOptions("-R -doNotMakeSplitPointActualValue");
		option4.addCVParameter("C 0.1 1.0 10");
		option4.addCVParameter("N 2 8 4");

		options.add(option1);
		options.add(option2);
		options.add(option3);
		options.add(option4);

		return options;
	}

	public static void main(String[] args) throws Exception {
		WekaEvaluator weka = new WekaEvaluator("src/main/resources/arff/Twitch/weka_testing/Training.arff",
				"src/main/resources/arff/Twitch/weka_testing/Test.arff");

		List<MyClassifier> classifiers = new ArrayList<MyClassifier>();
		MyClassifier part = new MyPART(new PART());
		classifiers.add(part);

		weka.optimizeParameters(classifiers);
		// weka.evaluateAll(classifiers);
	}

}
