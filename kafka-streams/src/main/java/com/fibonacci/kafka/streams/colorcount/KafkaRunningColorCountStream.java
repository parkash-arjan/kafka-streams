package com.fibonacci.kafka.streams.colorcount;

import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KGroupedTable;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.kstream.KTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KafkaRunningColorCountStream {

	@Autowired
	private Properties properties;

	@Value("${color.count.input.topic}")
	private String colorCountInputTopic;

	@Value("${color.count.output.topic}")
	private String colorCountOutputTopic;

	public void start() {
		KStreamBuilder kStreamBuilder = new KStreamBuilder();

		KStream<String, String> linesOfText = kStreamBuilder.stream(colorCountInputTopic);
		linesOfText.print();// null, PARKASH,GREEN

		KStream<String, String> filterLinesOfText = linesOfText.filter((key, value) -> value.split(",").length == 2);
		filterLinesOfText.print();// null, PARKASH,GREEN

		KStream<String, String> linesOftextWithKey = filterLinesOfText.selectKey((key, value) -> value.split(",")[0].toLowerCase());
		linesOftextWithKey.print();// parkash, PARKASH,GREEN

		KStream<String, String> mapValues = linesOftextWithKey.mapValues(value -> value.split(",")[1].toLowerCase());
		mapValues.print();// parkash, green

		KStream<String, String> validColors = mapValues.filter((user, color) -> Arrays.asList("red", "green", "blue").contains(color));
		validColors.print();

		validColors.to("user-color-intermediate-topic");

		KTable<String, String> kTable = kStreamBuilder.table("user-color-intermediate-topic");
		kTable.print();

		KGroupedTable<String, String> groupBy = kTable.groupBy((user, color) -> new KeyValue<>(color, color));
		
		KTable<String, Long> userColorGroupByCount = groupBy.count("user-color-groupBy");
		userColorGroupByCount.print();
		
		
		userColorGroupByCount.to(Serdes.String(), Serdes.Long(), colorCountOutputTopic);

		KafkaStreams kafkaStreams = new KafkaStreams(kStreamBuilder, properties);
		kafkaStreams.start();

		// shutdown hook to correctly close the streams application
		Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));

	}

}
