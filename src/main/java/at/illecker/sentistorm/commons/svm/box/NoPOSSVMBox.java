package at.illecker.sentistorm.commons.svm.box;

import at.illecker.sentistorm.commons.Dataset;
import at.illecker.sentistorm.commons.featurevector.nopos.NoPOSFeatureVectorGenerator;
import at.illecker.sentistorm.commons.svm.model.NoPOSModelTrainer;
import at.illecker.sentistorm.commons.svm.prediction.NoPOSPredictor;

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
