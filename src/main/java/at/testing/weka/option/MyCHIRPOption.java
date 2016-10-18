package at.testing.weka.option;

import weka.classifiers.misc.CHIRP;
import weka.core.Instances;
import weka.core.Utils;

public class MyCHIRPOption extends MyOption {

	private CHIRP chirp;

	public MyCHIRPOption(CHIRP chirp, Instances trainingsData) throws Exception {
		super(chirp, trainingsData);
		this.chirp = chirp;
	}

	public MyCHIRPOption(CHIRP chirp, Instances trainingsData, int numFolds) throws Exception {
		super(chirp, trainingsData, numFolds);
		this.chirp = chirp;
	}

	public MyCHIRPOption(CHIRP chirp, String[] options, Instances trainingsData, int numFolds) throws Exception {
		super(chirp, options, trainingsData, numFolds);
		this.chirp = chirp;
	}

	public MyCHIRPOption(CHIRP chirp, String[] options, Instances trainingsData) throws Exception {
		super(chirp, options, trainingsData);
		this.chirp = chirp;
	}

	public MyCHIRPOption(CHIRP chirp, String options, Instances trainingsData, int numFolds) throws Exception {
		super(chirp, options, trainingsData, numFolds);
		this.chirp = chirp;
	}

	public MyCHIRPOption(CHIRP chirp, String options, Instances trainingsData) throws Exception {
		super(chirp, options, trainingsData);
		this.chirp = chirp;
	}

	@Override
	public void setOptions(String[] options) throws Exception {
		chirp.setOptions(options);
	}

	@Override
	public void setOptions(String options) throws Exception {
		chirp.setOptions(Utils.splitOptions(options));
	}

}
