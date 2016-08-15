package at.illecker.sentistorm.drpc;

import org.apache.storm.Config;
import org.apache.storm.thrift.TException;
import org.apache.storm.thrift.transport.TTransportException;
import org.apache.storm.utils.DRPCClient;

public class DRPCTest {

	private static final String MSG_FIRST_PART = "{\"msg\":\"Kreygasm\",\"user\":\"theUser\",\"channel\":\"TheChannel\",\"timestamp\":";
	private static final String MSG_SECOND_PART = "}";

	public static void main(String[] args) {
		 Runnable r1 = new MyTestDRPCClient(0);
		 Thread t1 = new Thread(r1);
		 t1.start();

//		 Runnable r2 = new MyTestDRPCClient(100000);
//		 Thread t2 = new Thread(r2);
//		 t2.start();
//		 
//		 Runnable r3 = new MyTestDRPCClient(200000);
//		 Thread t3 = new Thread(r3);
//		 t3.start();
//
//		 Runnable r4 = new MyTestDRPCClient(300000);
//		 Thread t4 = new Thread(r4);
//		 t4.start();
	}

	static class MyTestDRPCClient implements Runnable {
		private long timestampOffset;

		public MyTestDRPCClient(long timestampOffset) {
			this.timestampOffset = timestampOffset;
		}

		@Override
		public void run() {
			DRPCClient client;
			try {
				System.out.println("RUN WITH OFFSET: " + timestampOffset + " STARTED");
				client = getDRPCClient();
				long timestamp = System.currentTimeMillis();
				for (int i = 0; i < 1000; i++) {
					String result = client.execute("getSentiment",
							MSG_FIRST_PART + (timestamp + timestampOffset + i) + MSG_SECOND_PART);
					System.out.println(result);
				}
				System.out.println("RUN WITH OFFSET: " + timestampOffset + " FINISHED");
			} catch (TException e) {
				e.printStackTrace();
			}
		}

		@SuppressWarnings("deprecation")
		private DRPCClient getDRPCClient() throws TTransportException {
			Config conf = new Config();
			conf.setDebug(false);
			conf.put("storm.thrift.transport", "org.apache.storm.security.auth.SimpleTransportPlugin");
			conf.put(Config.STORM_NIMBUS_RETRY_TIMES, 3);
			conf.put(Config.STORM_NIMBUS_RETRY_INTERVAL, 10);
			conf.put(Config.STORM_NIMBUS_RETRY_INTERVAL_CEILING, 20);
			conf.put(Config.DRPC_MAX_BUFFER_SIZE, 1048576);

			return new DRPCClient(conf, "localhost", 3772, 60000);
		}
	}

}
