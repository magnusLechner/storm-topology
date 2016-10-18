package at.testing.weka.classifier;

import java.util.ArrayList;
import java.util.List;

import at.testing.weka.WekaEvaluator;
import at.testing.weka.option.MyLibSVMOption;
import at.testing.weka.option.MyOption;
import at.testing.weka.statistic.MyEvaluation;
import weka.classifiers.functions.LibSVM;
import weka.core.Instances;
import weka.core.Utils;

public class MyLibSVM extends MyClassifier {

	private LibSVM libsvm;

	public MyLibSVM(LibSVM libsvm) {
		this(libsvm, "LibSVM");
	}

	public MyLibSVM(LibSVM libsvm, String name) {
		super(libsvm, name);
		this.libsvm = libsvm;
		addTestOptions();
	}

	public void addTestOptions() {
		try {

			String[] options1 = Utils.splitOptions("-C 2048 -G 0.000122140625");

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
		libsvm.setOptions(options);
	}

	@Override
	public String getCompleteCurrentOption() {
		String[] option = libsvm.getOptions();
		String res = "";
		for (String s : option) {
			res += s + " ";
		}
		return res;
	}

	@Override
	public List<MyOption> defineOptionsForOptimization(Instances trainingsData) throws Exception {
		List<MyOption> options = new ArrayList<MyOption>();

		MyLibSVMOption option1 = new MyLibSVMOption(new LibSVM(), trainingsData);
		option1.addCVParameter("C 64 2048 6");
		option1.addCVParameter("G 0.0001 0.1 10");

		options.add(option1);

		return options;
	}

	public static void main(String[] args) throws Exception {
		WekaEvaluator weka = new WekaEvaluator("src/main/resources/arff/Twitch/weka_testing/complete/Training.arff",
				"src/main/resources/arff/Twitch/weka_testing/complete/Test.arff");

		List<MyClassifier> classifiers = new ArrayList<MyClassifier>();
		MyClassifier libsvm = new MyLibSVM(new LibSVM());
		classifiers.add(libsvm);

//		weka.optimizeParameters(classifiers);
		List<List<MyEvaluation>> result = weka.evaluateAll(classifiers);
		
		for(List<MyEvaluation> options : result) {
			for(MyEvaluation evaluation : options) {
				System.out.println(evaluation.getEvaluation().toSummaryString());
			}
		}
	}

}
