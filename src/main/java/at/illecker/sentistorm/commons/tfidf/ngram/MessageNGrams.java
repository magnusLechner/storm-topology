package at.illecker.sentistorm.commons.tfidf.ngram;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ngram.NGramTokenizer;
import org.apache.lucene.analysis.shingle.ShingleFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.AttributeSource.AttributeFactory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.illecker.sentistorm.commons.tfidf.TfIdf;
import at.illecker.sentistorm.commons.tfidf.TfIdfNormalization;
import at.illecker.sentistorm.commons.tfidf.TfType;

public class MessageNGrams {

	private static final Logger LOG = LoggerFactory.getLogger(MessageNGrams.class);

	private TfType m_tfType;
	private TfIdfNormalization m_tfIdfNormalization;
	private List<Map<String, Double>> m_termFreqs;
	private Map<String, Double> m_inverseDocFreq;
	private Map<String, Integer> m_termIds;

	private static StringBuilder sb = new StringBuilder();
	
	private MessageNGrams(TfType type, TfIdfNormalization normalization) {
		this.m_tfType = type;
		this.m_tfIdfNormalization = normalization;
	}

	public TfType getTfType() {
		return m_tfType;
	}

	public TfIdfNormalization getTfIdfNormalization() {
		return m_tfIdfNormalization;
	}

	public List<Map<String, Double>> getTermFreqs() {
		return m_termFreqs;
	}

	public Map<String, Double> getInverseDocFreq() {
		return m_inverseDocFreq;
	}

	public Map<String, Integer> getTermIds() {
		return m_termIds;
	}

	public Map<String, Double> tfIdfFromTokens(List<String> preprocessedTweet) {
		return TfIdf.tfIdf(tfFromTokens(preprocessedTweet, m_tfType), m_inverseDocFreq, m_tfIdfNormalization);
	}

	public static MessageNGrams createFromTokens(List<List<String>> preprocessedTweets) {
		return createFromTokens(preprocessedTweets, TfType.RAW, TfIdfNormalization.COS);
	}

	public static MessageNGrams createFromTokens(List<List<String>> preprocessedTweets, TfType type,
			TfIdfNormalization normalization) {

		MessageNGrams messageNGrams = new MessageNGrams(type, normalization);

		messageNGrams.m_termFreqs = tfTokenTweets(preprocessedTweets, type);
		messageNGrams.m_inverseDocFreq = idf(messageNGrams.m_termFreqs);

		messageNGrams.m_termIds = new HashMap<String, Integer>();
		int i = 0;
		for (String key : messageNGrams.m_inverseDocFreq.keySet()) {
			messageNGrams.m_termIds.put(key, i);
			i++;
		}

		LOG.info("Found " + messageNGrams.m_inverseDocFreq.size() + " terms");
		// Debug
		// print("Term Frequency", m_termFreqs, m_inverseDocFreq);
		// print("Inverse Document Frequency", m_inverseDocFreq);
		return messageNGrams;
	}

	public static List<Map<String, Double>> tfTokenTweets(List<List<String>> preprocessedTweets, TfType type) {
		List<Map<String, Double>> termFreqs = new ArrayList<Map<String, Double>>();
		for (List<String> tweet : preprocessedTweets) {
			termFreqs.add(tfFromTokens(tweet, type));
		}
		return termFreqs;
	}

	public static Map<String, Double> tfFromTokens(List<String> preprocessedTokens, TfType type) {
		Map<String, Double> termFreq = new LinkedHashMap<String, Double>();

		List<String> words = new ArrayList<String>();
		for (String token : preprocessedTokens) {
			String word = token.toLowerCase();
			sb.append(word).append(" ");	
		}
		try {
			Reader reader = new StringReader(sb.toString());
			
			NGramTokenizer gramTokenizer = new NGramTokenizer(Version.LUCENE_46, reader, 3, 3);
			gramTokenizer.setReader(reader);
			CharTermAttribute charTermAttribute = gramTokenizer.addAttribute(CharTermAttribute.class);
			gramTokenizer.reset();

			while (gramTokenizer.incrementToken()) {
			    String nGram = charTermAttribute.toString();
			    words.add(nGram);
			}
			gramTokenizer.end();
			gramTokenizer.close();

//			TokenStream tokenizer = new StandardTokenizer(Version.LUCENE_46, reader);
//			tokenizer = new ShingleFilter(tokenizer, 3, 3);
//			CharTermAttribute charTermAttribute = tokenizer.addAttribute(CharTermAttribute.class);
//			tokenizer.reset();
//			
//			while (tokenizer.incrementToken()) {
//			    String termNGram = charTermAttribute.toString();
//			    words.add(termNGram);
//			}
//			tokenizer.end();
//			tokenizer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		termFreq = TfIdf.tf(termFreq, words);
		termFreq = TfIdf.normalizeTf(termFreq, type);
		return termFreq;
	}

	public static Map<String, Double> idf(List<Map<String, Double>> termFreq) {
		return TfIdf.idf(termFreq);
	}

	public static List<Map<String, Double>> tfIdf(List<Map<String, Double>> termFreqs,
			Map<String, Double> inverseDocFreq, TfIdfNormalization normalization) {

		List<Map<String, Double>> tfIdf = new ArrayList<Map<String, Double>>();
		// compute tfIdf for each document
		for (Map<String, Double> doc : termFreqs) {
			tfIdf.add(TfIdf.tfIdf(doc, inverseDocFreq, normalization));
		}

		return tfIdf;
	}

	public static void main(String[] args) throws IOException {

		List<String> preprocessedTweet = new ArrayList<String>();
		preprocessedTweet.add("This");
		preprocessedTweet.add("is");
		preprocessedTweet.add("a");
		preprocessedTweet.add("test");
		preprocessedTweet.add("String");
		
		for (String token : preprocessedTweet) {
			String word = token.toLowerCase();
			sb.append(word).append(" ");
		}
		
//		Reader reader = new StringReader(sb.toString());
//		NGramTokenizer gramTokenizer = new NGramTokenizer(Version.LUCENE_46, reader, 3, 3);
//		gramTokenizer.setReader(reader);
//		CharTermAttribute charTermAttribute = gramTokenizer.addAttribute(CharTermAttribute.class);
//		gramTokenizer.reset();
//
//		while (gramTokenizer.incrementToken()) {
//		    String token = charTermAttribute.toString();
//		    System.out.println(token);
//		}
//		gramTokenizer.end();
//		gramTokenizer.close();
//		
//		
//		System.out.println();
		
		Reader reader = new StringReader(sb.toString());
		TokenStream tokenizer = new StandardTokenizer(Version.LUCENE_46, reader);
		tokenizer = new ShingleFilter(tokenizer, 3, 3);
		CharTermAttribute charTermAttribute = tokenizer.addAttribute(CharTermAttribute.class);
		tokenizer.reset();
		
		while (tokenizer.incrementToken()) {
		    String token = charTermAttribute.toString();
		    System.out.println(token);
		}
		tokenizer.end();
		tokenizer.close();
	}
	
}
