package com.maciejwalkowiak.spring.http.autoconfigure;

import java.util.Map;

import com.maciejwalkowiak.spring.http.WebClientsProperties;
import org.junit.jupiter.api.Test;

import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;

class WebClientsAutoConfigurationTests {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(WebClientsAutoConfiguration.class, WebClientAutoConfiguration.class));

    @Test
    void createsWebClientsFromProperties() {
        contextRunner.withPropertyValues("http.clients.foo.url=http://foo.com/").run(ctx -> {
            assertThat(ctx).hasSingleBean(WebClientsProperties.class);
            WebClientsProperties props = ctx.getBean(WebClientsProperties.class);
            assertThat(props.getClients()).containsKey("foo");
            assertThat(props.getClients().get("foo").getUrl()).isEqualTo("http://foo.com/");

            Map<String, WebClient> clients = ctx.getBeansOfType(WebClient.class);
            assertThat(clients).containsKey("foo.WebClient");
        });
    }

}
