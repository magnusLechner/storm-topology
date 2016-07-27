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
package at.illecker.sentistorm.commons.featurevector.nopos;

import java.util.List;
import java.util.Map;

import at.illecker.sentistorm.commons.tfidf.nopos.NoPOSTweetTfIdf;

public class NoPOSCombinedFeatureVectorGenerator extends NoPOSFeatureVectorGenerator {

	private NoPOSSentimentFeatureVectorGenerator m_sentimentFeatureVectorGenerator = null;
	private NoPOSTfIdfFeatureVectorGenerator m_tfidfNoPOSFVG = null;
	private NoPOSUndefinedTwitchFeatureVectorGenerator m_undefinedTwitchFVG = null;

	public NoPOSCombinedFeatureVectorGenerator(boolean normalizePOSCounts, NoPOSTweetTfIdf tweetTfIdfNoPOS) {
		m_sentimentFeatureVectorGenerator = new NoPOSSentimentFeatureVectorGenerator(1);
//
//		m_tfidfNoPOSFVG = new NoPOSTfIdfFeatureVectorGenerator(tweetTfIdfNoPOS,
//				m_sentimentFeatureVectorGenerator.getFeatureVectorSize() + 1);
//
//		m_undefinedTwitchFVG = new NoPOSUndefinedTwitchFeatureVectorGenerator(
//				m_sentimentFeatureVectorGenerator.getFeatureVectorSize() + m_tfidfNoPOSFVG.getFeatureVectorSize() + 1);
		
		//TODO remove - only testing
//		m_tfidfNoPOSFVG = new NoPOSTfIdfFeatureVectorGenerator(tweetTfIdfNoPOS, 1);
	}

	@Override
	public int getFeatureVectorSize() {
		return 
				m_sentimentFeatureVectorGenerator.getFeatureVectorSize() 
//				+ m_tfidfNoPOSFVG.getFeatureVectorSize()
//				+ m_undefinedTwitchFVG.getFeatureVectorSize()
//				m_tfidfNoPOSFVG.getFeatureVectorSize()
		;
	}

	@Override
	public Map<Integer, Double> generateFeatureVector(List<String> tweet) {
		Map<Integer, Double> featureVector = m_sentimentFeatureVectorGenerator.generateFeatureVector(tweet);
//
//		featureVector.putAll(m_tfidfNoPOSFVG.generateFeatureVector(tweet));
//
//		featureVector.putAll(m_undefinedTwitchFVG.generateFeatureVector(tweet));
		
		//TODO remove - only testing
//		Map<Integer, Double> featureVector = m_tfidfNoPOSFVG.generateFeatureVector(tweet);

		return featureVector;
	}

}
