package com.fibonacci.kafka.streams.kafkastreams;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.fibonacci.kafka.streams.wordcount.KafkaWordCountStream;

@ComponentScan({ "com.fibonacci.kafka.streams.wordcount", "com.fibonacci.kafka.streams.config" })
@SpringBootApplication
public class KafkaStreamsApplication implements CommandLineRunner {

	@Autowired
	KafkaWordCountStream kafkaWordCountStream;

	public static void main(String[] args) {
		SpringApplication.run(KafkaStreamsApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		kafkaWordCountStream.wordCountStream();
	}
}
