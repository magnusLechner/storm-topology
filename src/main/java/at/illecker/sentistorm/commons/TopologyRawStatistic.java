package at.illecker.sentistorm.commons;

import java.io.Serializable;
import java.util.List;

import org.apache.storm.tuple.Values;

public class TopologyRawStatistic extends Values implements Serializable {
	
	private static final long serialVersionUID = -5539855980707403196L;
	private int tupelInTopologyCount;
	private List<Long> cycleTimes;
	
	public TopologyRawStatistic(int tupelInTopologyCount, List<Long> cycleTimes) {
		super();
		this.tupelInTopologyCount = tupelInTopologyCount;
		this.cycleTimes = cycleTimes;
	}
	
	public int getTupelInTupologyCount() {
		return tupelInTopologyCount;
	}
	
	public List<Long> getCycleTimes() {
		return cycleTimes;
	}
	
}
