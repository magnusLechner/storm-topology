package at.testing.weka.statistic;

import java.util.ArrayList;
import java.util.List;

public class OptionWrapper {

	private String option;
	private List<WekaStatistic> splits = new ArrayList<WekaStatistic>();

	public void addSplit(WekaStatistic weka) {
		splits.add(weka);
	}

	public WekaStatistic getSplit(int index) {
		return splits.get(index);
	}

	public List<WekaStatistic> getSplits() {
		return splits;
	}

	public String getOptionName() {
		return option;
	}

	public void setOption(String option) {
		this.option = option;
	}

}
