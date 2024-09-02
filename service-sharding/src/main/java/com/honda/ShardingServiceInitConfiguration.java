package com.honda;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@PropertySource(value = {"classpath:application-service-sharding.properties", "classpath:application-sensitiveness.properties"})
@Configuration
public class ShardingServiceInitConfiguration {
	
}