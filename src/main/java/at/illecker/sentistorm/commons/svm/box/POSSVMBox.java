package at.illecker.sentistorm.commons.svm.box;

import at.illecker.sentistorm.commons.Dataset;
import at.illecker.sentistorm.commons.featurevector.pos.FeatureVectorGenerator;
import at.illecker.sentistorm.commons.svm.model.POSModelTrainer;
import at.illecker.sentistorm.commons.svm.prediction.POSPredictor;

public class POSSVMBox extends SVMBox {

	public POSSVMBox(Dataset dataset, Class<? extends FeatureVectorGenerator> fvgClass, int nFold,
			boolean generateARFF) {
		POSModelTrainer posModelTrainer = new POSModelTrainer(dataset, fvgClass, nFold, generateARFF);
		POSPredictor posPredictor = new POSPredictor(posModelTrainer.generateModel(), fvgClass, dataset, generateARFF);
		setModelTrainer(posModelTrainer);
		setPredictor(posPredictor);
	}

	public POSSVMBox(Dataset dataset, FeatureVectorGenerator fvg, int nFold, boolean generateARFF) {
		POSModelTrainer posModelTrainer = new POSModelTrainer(dataset, fvg, nFold, generateARFF);
		POSPredictor posPredictor = new POSPredictor(posModelTrainer.generateModel(), fvg, dataset, generateARFF);
		setModelTrainer(posModelTrainer);
		setPredictor(posPredictor);
	}

}
