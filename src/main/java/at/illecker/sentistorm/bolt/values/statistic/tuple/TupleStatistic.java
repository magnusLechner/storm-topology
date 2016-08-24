package at.illecker.sentistorm.bolt.values.statistic.tuple;

public class TupleStatistic {
	
	//see storm UI
//	private long jsonStart;
//	private long jsonEnd;
//	
//	private long tokenzierStart;
//	private long tokenzierEnd;
//	
//	private long preprocessorStart;
//	private long preprocessorEnd;
//	
//	private long posTaggerStart;
//	private long posTaggerEnd;
//	
//	private long featureGeneratorStart;
//	private long featureGeneratorEnd;
//	
//	private long svmStart;
//	private long svmEnd;
//	
//	private long redisPublishStart;
//	private long redisPublishEnd;
	
	private long pipelineStart;
	private long pipelineEnd;
	
	public long getPipelineStart() {
		return pipelineStart;
	}
	
	public void setPipelineStart(long pipelineStart) {
		this.pipelineStart = pipelineStart;
	}
	
	public long getPipelineEnd() {
		return pipelineEnd;
	}
	
	public void setPipelineEnd(long pipelineEnd) {
		this.pipelineEnd = pipelineEnd;
	}

}
