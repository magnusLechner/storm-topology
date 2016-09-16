package at.lechner.weka.classifier;

import java.util.ArrayList;
import java.util.List;

import at.lechner.weka.WekaEvaluator;
import at.lechner.weka.option.MyLMTOption;
import at.lechner.weka.option.MyOption;
import weka.classifiers.trees.LMT;
import weka.core.Instances;
import weka.core.Utils;

public class MyLMT extends MyClassifier {

	private LMT lmt;

	public MyLMT(LMT lmt) {
		this(lmt, "LMT");
	}

	public MyLMT(LMT lmt, String name) {
		super(lmt, name);
		this.lmt = lmt;
		addTestOptions();
	}

	public void addTestOptions() {
		try {

			// TODO

//			String[] options1 = Utils.splitOptions("-F 1 -M 1.5");
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
		lmt.setOptions(options);
	}

	@Override
	public String getCompleteCurrentOption() {
		String[] option = lmt.getOptions();
		String res = "";
		for (String s : option) {
			res += s + " ";
		}
		return res;
	}

	@Override
	public List<MyOption> defineOptionsForOptimization(Instances trainingsData) throws Exception {
		List<MyOption> options = new ArrayList<MyOption>();

		MyLMTOption option1 = new MyLMTOption(new LMT(), trainingsData);
		option1.setOptions("-C");
		option1.addCVParameter("M 5 25 5");

		MyLMTOption option2 = new MyLMTOption(new LMT(), trainingsData);
		option2.setOptions("-C -A");
		option2.addCVParameter("M 5 25 5");

		MyLMTOption option3 = new MyLMTOption(new LMT(), trainingsData);
		option3.addCVParameter("I 20 100 5");
		option3.addCVParameter("M 5 25 5");

		MyLMTOption option4 = new MyLMTOption(new LMT(), trainingsData);
		option4.setOptions("-A");
		option4.addCVParameter("I 20 100 5");
		option4.addCVParameter("M 5 25 5");

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
		MyClassifier lmt = new MyLMT(new LMT());
		classifiers.add(lmt);

		weka.optimizeParameters(classifiers);
		// weka.evaluateAll(classifiers);
	}

}
