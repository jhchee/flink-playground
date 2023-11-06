package github.jhchee.apps;

import com.fasterxml.jackson.databind.ObjectMapper;
import github.jhchee.KafkaUtils;
import github.jhchee.events.TransactionEvent;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

import java.time.Duration;

public class CountTransactionByUserId {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStream<String> transactionSource = env.fromSource(
                KafkaUtils.createKafkaSource("count-transaction", "transaction-event"),
                WatermarkStrategy.forBoundedOutOfOrderness(Duration.ofMinutes(1)),
                "Kafka transaction source");

        DataStream<Tuple2<String, Integer>> transactionCountByUserId = transactionSource.map(new TransactionEventMapper())
                                                                                        .flatMap(new Tokenizer())
                                                                                        .keyBy(v -> v.f0)
                                                                                        .sum(1);
        transactionCountByUserId.print();
        env.execute("CountTransactionByUserId");
    }

    public static class TransactionEventMapper implements MapFunction<String, TransactionEvent> {
        @Override
        public TransactionEvent map(String payload) throws Exception {
            return mapper.readValue(payload, TransactionEvent.class);
        }
    }

    public static final class Tokenizer implements FlatMapFunction<TransactionEvent, Tuple2<String, Integer>> {
        @Override
        public void flatMap(TransactionEvent value, Collector<Tuple2<String, Integer>> out) {
            out.collect(new Tuple2<>(value.getUserId(), 1));
        }
    }

}
