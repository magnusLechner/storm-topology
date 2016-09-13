package at.lechner.weka.classifier;

import java.util.ArrayList;
import java.util.List;

import at.lechner.weka.WekaEvaluator;
import at.lechner.weka.option.MyJ48Option;
import at.lechner.weka.option.MyOption;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.Utils;

public class MyJ48 extends MyClassifier {

	private J48 j48;

	public MyJ48(J48 j48) {
		this(j48, "J48");
	}

	public MyJ48(J48 j48, String name) {
		super(j48, name);
		this.j48 = j48;
		addTestOptions();
	}

	public void addTestOptions() {
		try {
//			String[] options1 = Utils.splitOptions("-A");
//			addOption(options1);
//			String[] options2 = Utils.splitOptions("-C 0.5 -A");
//			addOption(options2);
//			String[] options3 = Utils.splitOptions("-C 0.5 -B -A");
//			addOption(options3);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setCurrentOption(String[] options) throws Exception {
		j48.setOptions(options);
	}

	@Override
	public String getCompleteCurrentOption() {
		String[] option = j48.getOptions();
		String res = "";
		for (String s : option) {
			res += s + " ";
		}
		return res;
	}

	@Override
	public List<MyOption> defineOptions(Instances trainingsData) throws Exception {
		List<MyOption> options = new ArrayList<MyOption>();

		MyJ48Option option1 = new MyJ48Option(new J48(), trainingsData);
		option1.addCVParameter("C 0.05 0.5 10");

		MyJ48Option option2 = new MyJ48Option(new J48(), trainingsData);
		option2.setOptions("-R");
		option2.addCVParameter("N 2 5 4");

		options.add(option1);
		options.add(option2);

		return options;
	}

	public static void main(String[] args) throws Exception {
		WekaEvaluator weka = new WekaEvaluator("src/main/resources/arff/Twitch/weka_testing/Training.arff",
				"src/main/resources/arff/Twitch/weka_testing/Test.arff");

		List<MyClassifier> classifiers = new ArrayList<MyClassifier>();
		MyClassifier j48 = new MyJ48(new J48());
		classifiers.add(j48);

		weka.optimizeParameters(classifiers);
		// weka.evaluateAll(classifiers);
	}

}
