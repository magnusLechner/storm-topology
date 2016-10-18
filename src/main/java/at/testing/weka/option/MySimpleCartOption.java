package at.testing.weka.option;

import weka.classifiers.trees.SimpleCart;
import weka.core.Instances;
import weka.core.Utils;

public class MySimpleCartOption extends MyOption {

	private SimpleCart simpleCart;

	public MySimpleCartOption(SimpleCart simpleCart, Instances trainingsData) throws Exception {
		super(simpleCart, trainingsData);
		this.simpleCart = simpleCart;
	}

	public MySimpleCartOption(SimpleCart simpleCart, Instances trainingsData, int numFolds) throws Exception {
		super(simpleCart, trainingsData, numFolds);
		this.simpleCart = simpleCart;
	}

	public MySimpleCartOption(SimpleCart simpleCart, String[] options, Instances trainingsData, int numFolds)
			throws Exception {
		super(simpleCart, options, trainingsData, numFolds);
		this.simpleCart = simpleCart;
	}

	public MySimpleCartOption(SimpleCart simpleCart, String[] options, Instances trainingsData) throws Exception {
		super(simpleCart, options, trainingsData);
		this.simpleCart = simpleCart;
	}

	public MySimpleCartOption(SimpleCart simpleCart, String options, Instances trainingsData, int numFolds)
			throws Exception {
		super(simpleCart, options, trainingsData, numFolds);
		this.simpleCart = simpleCart;
	}

	public MySimpleCartOption(SimpleCart simpleCart, String options, Instances trainingsData) throws Exception {
		super(simpleCart, options, trainingsData);
		this.simpleCart = simpleCart;
	}

	@Override
	public void setOptions(String[] options) throws Exception {
		simpleCart.setOptions(options);
	}

	@Override
	public void setOptions(String options) throws Exception {
		simpleCart.setOptions(Utils.splitOptions(options));
	}

}
