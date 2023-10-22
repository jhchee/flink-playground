package github.jhchee;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.Collection;

public class CountWord {

    public static void main(String[] args) throws Exception {
        Collection<String> examples = new ArrayList<>();
        examples.add("the last human");
        examples.add("the age of apes");
        examples.add("around the world in 80 days");
        examples.add("wide range of topics");

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<String> dataStream = env.fromCollection(examples);

        DataStream<Tuple2<String, Integer>> counts = dataStream.flatMap(new Tokenizer())
                .keyBy(v -> v.f0)
                .sum(1);

        counts.print();
        env.execute();
    }

    public static final class Tokenizer implements FlatMapFunction<String, Tuple2<String, Integer>> {

        @Override
        public void flatMap(String value, Collector<Tuple2<String, Integer>> out) {
            // normalize and split the line
            String[] tokens = value.toLowerCase().split("\\W+");

            // emit the pairs
            for (String token : tokens) {
                if (token.length() > 0) {
                    out.collect(new Tuple2<>(token, 1));
                }
            }
        }
    }
}
