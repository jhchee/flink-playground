package github.jhchee.apps;

import github.jhchee.events.AlertEvent;
import github.jhchee.events.TransactionEvent;
import github.jhchee.events.UserEvent;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.cep.CEP;
import org.apache.flink.cep.PatternStream;
import org.apache.flink.cep.nfa.aftermatch.AfterMatchSkipStrategy;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.SimpleCondition;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.PrintSinkFunction;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.util.List;
import java.util.Map;

public class FraudDetectionRunner1 {
    public static void main(String[] args) throws Exception {
        FraudDetector1 detector = new FraudDetector1(new LowHighSpendingEventSource(), new PrintSinkFunction<>());
        detector.execute();
    }
    public static class FraudDetector1 {
        private final SourceFunction<UserEvent> source;
        private final SinkFunction<AlertEvent> sink;

        public FraudDetector1(SourceFunction<UserEvent> source, SinkFunction<AlertEvent> sink) {
            this.source = source;
            this.sink = sink;
        }

        public static Pattern<UserEvent, TransactionEvent> fraudPattern() {
            double SMALL_AMOUNT = 1;
            double BIG_AMOUNT = 1000;
            // Identify potential fraudulent activities by analyzing transaction amounts.
            // Fraudsters typically start with small transactions to test stolen credentials.
            // If successful, they proceed to larger purchases or withdrawals.
            // What is considered small or large depends on the account history.
            return Pattern.<UserEvent>begin("low-spending-group", AfterMatchSkipStrategy.skipPastLastEvent())
                          .subtype(TransactionEvent.class)
                          .where(SimpleCondition.of(event -> event.getAmount() <= SMALL_AMOUNT))
                          .oneOrMore()
                          .next("high-spending-group")
                          .subtype(TransactionEvent.class)
                          .where(SimpleCondition.of(event -> event.getAmount() >= BIG_AMOUNT));
        }
        public void execute() throws Exception {
            StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
            WatermarkStrategy<UserEvent> strategy = WatermarkStrategy.<UserEvent>forMonotonousTimestamps()
                                                                     .withTimestampAssigner(
                                                                             (event, t) -> event.getEventTime().toEpochMilli()
                                                                     );
            KeyedStream<UserEvent, String> keyedEvent = env.addSource(source)
                                                           .assignTimestampsAndWatermarks(strategy)
                                                           .keyBy(UserEvent::getUserId);

            PatternStream<UserEvent> patternStream = CEP.pattern(keyedEvent, fraudPattern());
            DataStream<AlertEvent> output = patternStream.select(FraudDetector1::createAlert);
            output.addSink(sink);
            env.execute("");
        }

        public static AlertEvent createAlert(Map<String, List<UserEvent>> pattern) {
            int lowSpentSize = pattern.get("low-spending-group").size();
            TransactionEvent highSpent = (TransactionEvent) pattern.get("high-spending-group").get(0);
            double highSpentAmount = highSpent.getAmount();
            String userId = highSpent.getUserId();

            // severity is derived from how big the difference is between lowSpentAmount and highSpentAmount
            return new AlertEvent(userId, String.format("Bait and switch detected! Low Spent Size=%s, High Spent Amount=%s",
                    lowSpentSize, highSpentAmount), AlertEvent.RiskLevel.MEDIUM);
        }
    }
}
