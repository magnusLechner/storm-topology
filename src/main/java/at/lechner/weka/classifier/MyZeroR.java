package at.lechner.weka.classifier;

import java.util.ArrayList;
import java.util.List;

import at.lechner.weka.WekaEvaluator;
import at.lechner.weka.option.MyOption;
import at.lechner.weka.option.MyZeroROption;
import weka.classifiers.rules.ZeroR;
import weka.core.Instances;

public class MyZeroR extends MyClassifier {

	private ZeroR zeroR;

	public MyZeroR(ZeroR zeroR) {
		this(zeroR, "ZeroR");
	}

	public MyZeroR(ZeroR zeroR, String name) {
		super(zeroR, name);
		this.zeroR = zeroR;
		addTestOptions();
	}

	public void addTestOptions() {
		try {

			// There are no parameters for ZeroR

			if (getOptionsList().size() == 0) {
				System.err.println(getName() + ": No test options!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setCurrentOption(String[] options) throws Exception {
		zeroR.setOptions(options);
	}

	@Override
	public String getCompleteCurrentOption() {
		String[] option = zeroR.getOptions();
		String res = "";
		for (String s : option) {
			res += s + " ";
		}
		return res;
	}

	@Override
	public List<MyOption> defineOptionsForOptimization(Instances trainingsData) throws Exception {
		List<MyOption> options = new ArrayList<MyOption>();

		MyZeroROption option1 = new MyZeroROption(new ZeroR(), trainingsData);

		options.add(option1);

		return options;
	}

	public static void main(String[] args) throws Exception {
		WekaEvaluator weka = new WekaEvaluator("src/main/resources/arff/Twitch/weka_testing/Training.arff",
				"src/main/resources/arff/Twitch/weka_testing/Test.arff");

		List<MyClassifier> classifiers = new ArrayList<MyClassifier>();
		MyClassifier zeroR = new MyZeroR(new ZeroR());
		classifiers.add(zeroR);

		weka.optimizeParameters(classifiers);
		// weka.evaluateAll(classifiers);
	}

}
