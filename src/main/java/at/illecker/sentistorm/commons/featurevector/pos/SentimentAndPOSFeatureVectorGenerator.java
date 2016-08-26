/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package at.illecker.sentistorm.commons.featurevector.pos;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cmu.arktweetnlp.Tagger.TaggedToken;

public class SentimentAndPOSFeatureVectorGenerator extends FeatureVectorGenerator {
	private static final Logger LOG = LoggerFactory.getLogger(CombinedFeatureVectorGenerator.class);

	private SentimentFeatureVectorGenerator m_sentimentFeatureVectorGenerator = null;
	private POSFeatureVectorGenerator m_POSFeatureVectorGenerator = null;

	public SentimentAndPOSFeatureVectorGenerator(boolean normalizePOSCounts) {
		m_sentimentFeatureVectorGenerator = new SentimentFeatureVectorGenerator(1);

		m_POSFeatureVectorGenerator = new POSFeatureVectorGenerator(normalizePOSCounts,
				m_sentimentFeatureVectorGenerator.getFeatureVectorSize() + 1);

		LOG.info("VectorSize: " + getFeatureVectorSize());
	}

	@Override
	public int getFeatureVectorSize() {
		return m_sentimentFeatureVectorGenerator.getFeatureVectorSize()
				+ m_POSFeatureVectorGenerator.getFeatureVectorSize();
	}

	@Override
	public Map<Integer, Double> generateFeatureVector(List<TaggedToken> tweet) {
		Map<Integer, Double> featureVector = m_sentimentFeatureVectorGenerator.generateFeatureVector(tweet);

		featureVector.putAll(m_POSFeatureVectorGenerator.generateFeatureVector(tweet));

		return featureVector;
	}
	
}
