package at.lechner.weka.statistic;

import java.util.ArrayList;
import java.util.List;

public class ClassifierWrapper {

	private String name;
	private List<OptionWrapper> options = new ArrayList<OptionWrapper>();
	
	public void addOption(OptionWrapper option) {
		options.add(option);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<OptionWrapper> getOptions() {
		return options;
	}
	
	public OptionWrapper getOption(int index) {
		return options.get(index);
	}
	
}
