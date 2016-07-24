package at.illecker.sentistorm.drpc;

import org.apache.storm.Config;
import org.apache.storm.thrift.TException;
import org.apache.storm.utils.DRPCClient;

public class DRPCTest {

	public static void main(String[] args) {
		DRPCClient client;
		try {
			Config conf = new Config();
	        conf.setDebug(false);
	        //TODO SimpleTransportPlugin is deprecated
	        conf.put("storm.thrift.transport", "org.apache.storm.security.auth.SimpleTransportPlugin");
	        conf.put(Config.STORM_NIMBUS_RETRY_TIMES, 3);
	        conf.put(Config.STORM_NIMBUS_RETRY_INTERVAL, 10);
	        conf.put(Config.STORM_NIMBUS_RETRY_INTERVAL_CEILING, 20);
	        conf.put(Config.DRPC_MAX_BUFFER_SIZE, 1048576);

//			client = new DRPCClient(conf, "localhost", 3772, 60000);
			client = new DRPCClient(conf, "http://eventstorm-sentistorm.os.cggstack.cheergg.com", 3772, 60000);
			
			for(int i = 0; i < 1000; i++) {
				String result = client.execute("getSentiment", "{\"msg\":\"Kreygasm\"}");
				System.out.println(result);	
			}
//			while(true) {
//				String result = client.execute("getSentiment", "{\"msg\":\"Kreygasm\"}");
//				System.out.println(result);
//			}
			
		} catch (TException e) {
			e.printStackTrace();
		}
	}
	
}
