package at.lechner.weka.classifier;

import java.util.ArrayList;
import java.util.List;

import at.lechner.weka.WekaEvaluator;
import at.lechner.weka.option.MyCHIRPOption;
import at.lechner.weka.option.MyOption;
import weka.classifiers.misc.CHIRP;
import weka.core.Instances;
import weka.core.Utils;

public class MyCHIRP extends MyClassifier {

	private CHIRP chirp;

	public MyCHIRP(CHIRP chirp) {
		this(chirp, "CHIRP");
	}

	public MyCHIRP(CHIRP chirp, String name) {
		super(chirp, name);
		this.chirp = chirp;
		addTestOptions();
	}

	public void addTestOptions() {
		try {
			String[] options1 = Utils.splitOptions("-V 25 -S 1");

			addOption(options1);

			if (getOptionsList().size() == 0) {
				System.err.println(getName() + ": No test options!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setCurrentOption(String[] options) throws Exception {
		chirp.setOptions(options);
	}

	@Override
	public String getCompleteCurrentOption() {
		String[] option = chirp.getOptions();
		String res = "";
		for (String s : option) {
			res += s + " ";
		}
		return res;
	}

	@Override
	public List<MyOption> defineOptionsForOptimization(Instances trainingsData) throws Exception {
		List<MyOption> options = new ArrayList<MyOption>();

		MyCHIRPOption option1 = new MyCHIRPOption(new CHIRP(), trainingsData);
		option1.addCVParameter("V 50 300 6");

		options.add(option1);

		return options;
	}

	public static void main(String[] args) throws Exception {
		WekaEvaluator weka = new WekaEvaluator("src/main/resources/arff/Twitch/weka_testing/Training.arff",
				"src/main/resources/arff/Twitch/weka_testing/Test.arff");

		List<MyClassifier> classifiers = new ArrayList<MyClassifier>();
		MyClassifier chirp = new MyCHIRP(new CHIRP());
		classifiers.add(chirp);

		weka.optimizeParameters(classifiers);
		// weka.evaluateAll(classifiers);
	}

}
