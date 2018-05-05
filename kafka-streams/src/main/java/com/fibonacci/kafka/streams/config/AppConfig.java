package com.fibonacci.kafka.streams.config;

import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@PropertySource({ "classpath:application.properties" })
@Configuration
public class AppConfig {

	@Value("${application.id.config}")
	private String applicationIdConfig = "";

	@Value("${bootstrap.servers.config}")
	private String bootstrapServersConfig = "";

	@Value("${auto.offset.reset.config}")
	private String autoOffsetResetConfig = "";

	@Bean
	public Properties properties() {

		final Properties properties = new Properties();
		properties.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationIdConfig);
		properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServersConfig);
		properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetResetConfig);

		properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
		properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

		return properties;

	}

}
