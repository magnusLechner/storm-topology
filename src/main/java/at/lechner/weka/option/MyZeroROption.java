package at.lechner.weka.option;

import weka.classifiers.rules.ZeroR;
import weka.core.Instances;
import weka.core.Utils;

public class MyZeroROption extends MyOption {

	private ZeroR zeroR;

	public MyZeroROption(ZeroR zeroR, Instances trainingsData) throws Exception {
		super(zeroR, trainingsData);
		this.zeroR = zeroR;
	}

	public MyZeroROption(ZeroR zeroR, Instances trainingsData, int numFolds) throws Exception {
		super(zeroR, trainingsData, numFolds);
		this.zeroR = zeroR;
	}

	public MyZeroROption(ZeroR zeroR, String[] options, Instances trainingsData, int numFolds) throws Exception {
		super(zeroR, options, trainingsData, numFolds);
		this.zeroR = zeroR;
	}

	public MyZeroROption(ZeroR zeroR, String[] options, Instances trainingsData) throws Exception {
		super(zeroR, options, trainingsData);
		this.zeroR = zeroR;
	}

	public MyZeroROption(ZeroR zeroR, String options, Instances trainingsData, int numFolds) throws Exception {
		super(zeroR, options, trainingsData, numFolds);
		this.zeroR = zeroR;
	}

	public MyZeroROption(ZeroR zeroR, String options, Instances trainingsData) throws Exception {
		super(zeroR, options, trainingsData);
		this.zeroR = zeroR;
	}

	@Override
	public void setOptions(String[] options) throws Exception {
		zeroR.setOptions(options);
	}

	@Override
	public void setOptions(String options) throws Exception {
		zeroR.setOptions(Utils.splitOptions(options));
	}

}
