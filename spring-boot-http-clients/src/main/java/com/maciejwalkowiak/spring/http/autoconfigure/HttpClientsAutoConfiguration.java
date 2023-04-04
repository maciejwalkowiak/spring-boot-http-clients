package com.maciejwalkowiak.spring.http.autoconfigure;

import com.maciejwalkowiak.spring.http.registration.AutoConfigurationHttpClientsRegistrar;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.function.client.WebClient;

@Import({ AutoConfigurationHttpClientsRegistrar.class})
@ConditionalOnClass(WebClient.class)
public class HttpClientsAutoConfiguration {
}
