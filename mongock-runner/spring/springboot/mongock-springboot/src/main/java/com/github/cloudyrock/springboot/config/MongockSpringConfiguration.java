package com.github.cloudyrock.springboot.config;

import com.github.cloudyrock.spring.config.MongockSpringConfigurationBase;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("mongock")
public class MongockSpringConfiguration extends MongockSpringConfigurationBase {
}
