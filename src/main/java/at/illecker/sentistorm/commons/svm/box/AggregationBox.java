package at.illecker.sentistorm.commons.svm.box;

import java.util.List;

import at.illecker.sentistorm.commons.Dataset;
import at.illecker.sentistorm.commons.featurevector.aggregate.AggregationFeatureVectorGenerator;
import at.illecker.sentistorm.commons.svm.model.AggregationModelTrainer;
import at.illecker.sentistorm.commons.svm.prediction.AggregationPredictor;

public class AggregationBox extends SVMBox {

	public AggregationBox(List<SVMBox> boxes, Dataset dataset,
			Class<? extends AggregationFeatureVectorGenerator> aggregationFVGClass, int nFold, boolean generateARFF) {
		AggregationModelTrainer modelTrainer = new AggregationModelTrainer(boxes, dataset, aggregationFVGClass, nFold,
				generateARFF);
		AggregationPredictor predictor = new AggregationPredictor(boxes, modelTrainer.generateModel(),
				aggregationFVGClass, dataset, generateARFF);
		setModelTrainer(modelTrainer);
		setPredictor(predictor);
	}

	public AggregationBox(List<SVMBox> boxes, Dataset dataset, AggregationFeatureVectorGenerator aggregationFVG,
			int nFold, boolean generateARFF) {
		AggregationModelTrainer modelTrainer = new AggregationModelTrainer(boxes, dataset, aggregationFVG, nFold,
				generateARFF);
		AggregationPredictor predictor = new AggregationPredictor(boxes, modelTrainer.generateModel(), aggregationFVG,
				dataset, generateARFF);
		setModelTrainer(modelTrainer);
		setPredictor(predictor);
	}

}
