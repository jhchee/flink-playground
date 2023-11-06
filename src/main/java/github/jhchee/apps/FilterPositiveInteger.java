package github.jhchee.apps;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class FilterPositiveInteger {

	public static void main(String[] args) throws Exception {
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		DataStream<Long> stream = env.fromSequence(-10, 10).filter(x -> x >= 0);
		stream.print();

		env.execute("FilterPositiveInteger");
	}
}
