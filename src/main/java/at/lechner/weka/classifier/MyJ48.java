package at.lechner.weka.classifier;

import weka.classifiers.trees.J48;
import weka.core.Utils;

public class MyJ48 extends MyClassifier {

	private J48 j48;

	public MyJ48(J48 j48) {
		this(j48, "J48");
	}

	public MyJ48(J48 j48, String name) {
		super(j48, name);
		this.j48 = j48;

		addAllOptions();
	}

	public void addAllOptions() {
		try {
			String[] options1 = Utils.splitOptions("-U -B");
			addOption(options1);
			String[] options2 = Utils.splitOptions("-A");
			addOption(options2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setOption(String[] options) throws Exception {
		j48.setOptions(options);
	}

}
