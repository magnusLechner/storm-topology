package at.lechner.bolt;

import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.illecker.sentistorm.commons.SentimentClass;

public class PrinterBolt extends BaseBasicBolt {
	public static final String ID = "printer-bolt";
	public static final String CONF_LOGGING = ID + ".logging";
	private static final long serialVersionUID = -6069671099176334117L;
	public static final Logger LOG = LoggerFactory.getLogger(PrinterBolt.class);

	@Override
	public void execute(Tuple tuple, BasicOutputCollector collector) {
		String text = tuple.getStringByField("text");
		SentimentClass predictedSentiment = (SentimentClass)tuple.getValueByField("predictedSentiment");
		LOG.info("<<MYLOG>> TEXT: \t" + text + "  Sentiment: \t" + predictedSentiment);
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer arg0) {
		//no output here
	}
	
}
