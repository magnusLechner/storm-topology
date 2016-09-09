package at.lechner.weka.classifier;

import java.util.ArrayList;
import java.util.List;

import weka.classifiers.Classifier;
import weka.core.Instances;

public abstract class MyClassifier {

	private Classifier classifier;
	private List<String[]> optionsList;
	private String name;

	public MyClassifier(Classifier classifier) {
		this.classifier = classifier;
		this.optionsList = new ArrayList<String[]>();
	}

	public MyClassifier(Classifier classifier, String name) {
		this.classifier = classifier;
		this.optionsList = new ArrayList<String[]>();
		this.name = name;
	}

	public void buildClassifier(Instances trainingSet) throws Exception {
		classifier.buildClassifier(trainingSet);
	}

	public abstract void setOption(String[] options) throws Exception;

	public abstract void addTestOptions() throws Exception;

	public void addOption(String[] options) {
		optionsList.add(options);
	}

	public String[] getOption(int index) {
		String[] tmp = optionsList.get(index);
		String[] res = new String[tmp.length];
		for(int i = 0; i < tmp.length; i++) {
			res[i] = tmp[i];
		}
		return res;
	}

	public List<String[]> getOptionsList() {
		return optionsList;
	}

	public int getOptionsListSize() {
		return optionsList.size();
	}

	public String getName() {
		return name;
	}

	public Classifier getClassifier() {
		return classifier;
	}

	public void printOption(int index) {
		String[] option = getOption(index);
		String res = "";
		for (String s : option) {
			res += s + " ";
		}
		System.out.println(res);
	}

	public String getCompleteOption(int index) {
		String[] option = getOption(index);
		String res = "";
		for (String s : option) {
			res += s + " ";
		}
		return res;
	}

}
