package github.jhchee;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;

public class KafkaUtils {
    public static KafkaSource<String> createKafkaSource(String groupId, String topicName) {
        String boostrapServers = System.getenv().getOrDefault("KAFKA_BROKERS", "localhost:9092");
        return KafkaSource.<String>builder()
                          .setBootstrapServers(boostrapServers)
                          .setTopics(topicName)
                          .setClientIdPrefix(topicName)
                          .setGroupId(groupId)
                          .setStartingOffsets(OffsetsInitializer.committedOffsets(OffsetResetStrategy.EARLIEST))
                          .setValueOnlyDeserializer(new SimpleStringSchema())
                          .build();
    }
}
