package com.honda;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@PropertySource({"classpath:application-service-sharding.properties"})
@Configuration
public class ShardingServiceInitConfiguration {
	
}