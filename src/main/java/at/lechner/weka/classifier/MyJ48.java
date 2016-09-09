package at.lechner.weka.classifier;

import weka.classifiers.trees.J48;
import weka.core.Utils;

public class MyJ48 extends MyClassifier {

	private static final J48 j48 = new J48();

	public MyJ48() {
		this("J48");
	}

	public MyJ48(String name) {
		super(j48, name);

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
