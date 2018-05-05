package com.fibonacci.kafka.streams.wordcount;

import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.kstream.KTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KafkaWordCountStream {

	@Autowired
	private Properties properties;

	
	@Value("${word.count.input.topic}")
	private String wordCountInputTopic;

	@Value("${word.count.output.topic}")	
	private String wordCountOutputTopic;

	public void wordCountStream() {

		KStreamBuilder builder = new KStreamBuilder();

		KStream<String, String> linesOfText = builder.stream(wordCountInputTopic);				
		KStream<String, String> lowerCaseLines = linesOfText.mapValues(textLine -> textLine.toLowerCase());
		KStream<String, String> flatMapValues = lowerCaseLines.flatMapValues(textLine -> Arrays.asList(textLine.split(" ")));
		KStream<String, String> selectKey = flatMapValues.selectKey((key, word) -> word);
		KGroupedStream<String, String> groupByKey = selectKey.groupByKey();
		KTable<String, Long> wordCount = groupByKey.count("wordCount");
		wordCount.to(Serdes.String(), Serdes.Long(), wordCountOutputTopic);

		KafkaStreams kafkaStreams = new KafkaStreams(builder, properties);
		kafkaStreams.start();

		// shutdown hook to correctly close the streams application
		Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));

		// let's print the topology every 10 seconds for testing purposes
		while (true) {
			System.out.println(kafkaStreams.toString());
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				break;
			}
		}

	}

}
