package at.lechner.weka.option;

import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.core.Instances;
import weka.core.Utils;

public class MyNaiveBayesMultinomialOption extends MyOption {

	private NaiveBayesMultinomial naiveBayesMultinomial;

	public MyNaiveBayesMultinomialOption(NaiveBayesMultinomial naiveBayesMultinomial, Instances trainingsData)
			throws Exception {
		super(naiveBayesMultinomial, trainingsData);
		this.naiveBayesMultinomial = naiveBayesMultinomial;
	}

	public MyNaiveBayesMultinomialOption(NaiveBayesMultinomial naiveBayesMultinomial, Instances trainingsData,
			int numFolds) throws Exception {
		super(naiveBayesMultinomial, trainingsData, numFolds);
		this.naiveBayesMultinomial = naiveBayesMultinomial;
	}

	public MyNaiveBayesMultinomialOption(NaiveBayesMultinomial naiveBayesMultinomial, String[] options,
			Instances trainingsData, int numFolds) throws Exception {
		super(naiveBayesMultinomial, options, trainingsData, numFolds);
		this.naiveBayesMultinomial = naiveBayesMultinomial;
	}

	public MyNaiveBayesMultinomialOption(NaiveBayesMultinomial naiveBayesMultinomial, String[] options,
			Instances trainingsData) throws Exception {
		super(naiveBayesMultinomial, options, trainingsData);
		this.naiveBayesMultinomial = naiveBayesMultinomial;
	}

	public MyNaiveBayesMultinomialOption(NaiveBayesMultinomial naiveBayesMultinomial, String options,
			Instances trainingsData, int numFolds) throws Exception {
		super(naiveBayesMultinomial, options, trainingsData, numFolds);
		this.naiveBayesMultinomial = naiveBayesMultinomial;
	}

	public MyNaiveBayesMultinomialOption(NaiveBayesMultinomial naiveBayesMultinomial, String options,
			Instances trainingsData) throws Exception {
		super(naiveBayesMultinomial, options, trainingsData);
		this.naiveBayesMultinomial = naiveBayesMultinomial;
	}

	@Override
	public void setOptions(String[] options) throws Exception {
		naiveBayesMultinomial.setOptions(options);
	}

	@Override
	public void setOptions(String options) throws Exception {
		naiveBayesMultinomial.setOptions(Utils.splitOptions(options));
	}

}
