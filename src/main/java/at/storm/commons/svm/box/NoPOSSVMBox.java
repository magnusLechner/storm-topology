package at.storm.commons.svm.box;

import at.storm.commons.Dataset;
import at.storm.commons.featurevector.nopos.NoPOSFeatureVectorGenerator;
import at.storm.commons.svm.model.NoPOSModelTrainer;
import at.storm.commons.svm.prediction.NoPOSPredictor;

public class NoPOSSVMBox extends SVMBox {

	public NoPOSSVMBox(Dataset dataset, Class<? extends NoPOSFeatureVectorGenerator> noPOSFVGClass, int nFold,
			boolean generateARFF) {
		NoPOSModelTrainer noPOSModelTrainer = new NoPOSModelTrainer(dataset, noPOSFVGClass, nFold, generateARFF);
		NoPOSPredictor noPOSPredictor = new NoPOSPredictor(noPOSModelTrainer.generateModel(), noPOSFVGClass, dataset,
				generateARFF);
		setModelTrainer(noPOSModelTrainer);
		setPredictor(noPOSPredictor);
	}

	public NoPOSSVMBox(Dataset dataset, NoPOSFeatureVectorGenerator noPOSFVG, int nFold, boolean generateARFF) {
		NoPOSModelTrainer noPOSModelTrainer = new NoPOSModelTrainer(dataset, noPOSFVG, nFold, generateARFF);
		NoPOSPredictor noPOSPredictor = new NoPOSPredictor(noPOSModelTrainer.generateModel(), noPOSFVG, dataset,
				generateARFF);
		setModelTrainer(noPOSModelTrainer);
		setPredictor(noPOSPredictor);
	}
}
