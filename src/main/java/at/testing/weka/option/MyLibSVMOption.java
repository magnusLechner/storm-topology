package at.testing.weka.option;

import weka.classifiers.functions.LibSVM;
import weka.core.Instances;
import weka.core.Utils;

public class MyLibSVMOption extends MyOption {

	private LibSVM libsvm;

	public MyLibSVMOption(LibSVM libsvm, Instances trainingsData) throws Exception {
		super(libsvm, trainingsData);
		this.libsvm = libsvm;
	}

	public MyLibSVMOption(LibSVM libsvm, Instances trainingsData, int numFolds) throws Exception {
		super(libsvm, trainingsData, numFolds);
		this.libsvm = libsvm;
	}

	public MyLibSVMOption(LibSVM libsvm, String[] options, Instances trainingsData, int numFolds) throws Exception {
		super(libsvm, options, trainingsData, numFolds);
		this.libsvm = libsvm;
	}

	public MyLibSVMOption(LibSVM libsvm, String[] options, Instances trainingsData) throws Exception {
		super(libsvm, options, trainingsData);
		this.libsvm = libsvm;
	}

	public MyLibSVMOption(LibSVM libsvm, String options, Instances trainingsData, int numFolds) throws Exception {
		super(libsvm, options, trainingsData, numFolds);
		this.libsvm = libsvm;
	}

	public MyLibSVMOption(LibSVM libsvm, String options, Instances trainingsData) throws Exception {
		super(libsvm, options, trainingsData);
		this.libsvm = libsvm;
	}

	@Override
	public void setOptions(String[] options) throws Exception {
		libsvm.setOptions(options);
	}

	@Override
	public void setOptions(String options) throws Exception {
		libsvm.setOptions(Utils.splitOptions(options));
	}

}
