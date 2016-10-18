package at.storm.commons.svm.box;

import at.storm.commons.Dataset;
import at.storm.commons.featurevector.pos.FeatureVectorGenerator;
import at.storm.commons.svm.model.POSModelTrainer;
import at.storm.commons.svm.prediction.POSPredictor;

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
