package at.illecker.sentistorm.commons.featurevector.selector;

import java.util.List;

import at.illecker.sentistorm.commons.featurevector.aggregate.AggregationFeatureVectorGenerator;
import at.illecker.sentistorm.commons.featurevector.aggregate.ListResultsFeatureVectorGenerator;
import at.illecker.sentistorm.commons.featurevector.aggregate.OriginAndResultFeatureVectorGenerator;
import at.illecker.sentistorm.commons.svm.box.SVMBox;

public class AggregationFVGSelector {

	public static AggregationFeatureVectorGenerator selectFVG(List<SVMBox> boxes,
			Class<? extends AggregationFeatureVectorGenerator> aggregateFeatureVectorGenerator) {
		if (aggregateFeatureVectorGenerator.equals(ListResultsFeatureVectorGenerator.class)) {
			return new ListResultsFeatureVectorGenerator(boxes);
		}else if (aggregateFeatureVectorGenerator.equals(OriginAndResultFeatureVectorGenerator.class)) {
			return new OriginAndResultFeatureVectorGenerator(boxes);
		} else {
			throw new UnsupportedOperationException("AggregateFeatureVectorGenerator '"
					+ aggregateFeatureVectorGenerator.getName() + "' is not supported!");
		}
	}

}
