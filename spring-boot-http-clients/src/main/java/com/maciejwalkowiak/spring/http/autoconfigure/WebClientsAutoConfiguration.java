package com.maciejwalkowiak.spring.http.autoconfigure;

import com.maciejwalkowiak.spring.http.WebClientsProperties;
import com.maciejwalkowiak.spring.http.annotation.EnableWebClients;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.reactive.function.client.WebClient;

@AutoConfiguration(after = WebClientAutoConfiguration.class)
@ConditionalOnClass(WebClient.class)
@ConditionalOnBean(WebClient.Builder.class)
@EnableWebClients
@EnableConfigurationProperties(WebClientsProperties.class)
public class WebClientsAutoConfiguration {
}
