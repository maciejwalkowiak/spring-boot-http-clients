package integration;

import com.maciejwalkowiak.spring.http.annotation.HttpClient;
import com.maciejwalkowiak.spring.http.autoconfigure.HttpClientsAutoConfiguration;
import com.maciejwalkowiak.spring.http.autoconfigure.WebClientsAutoConfiguration;
import org.junit.jupiter.api.Test;

import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration;
import org.springframework.boot.context.annotation.UserConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.service.annotation.GetExchange;

import static org.assertj.core.api.Assertions.assertThat;

public class IntegrationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(HttpClientsAutoConfiguration.class, WebClientsAutoConfiguration.class, WebClientAutoConfiguration.class));

    @Test
    void createsWebClientsFromProperties() {
        contextRunner
                .withConfiguration(UserConfigurations.of(AppConfigWithoutWebClientBean.class))
                .withPropertyValues("http.clients.todo-client.url=http://foo.com/")
                .run(ctx -> assertThat(ctx.getBean(TodoClient.class)).isNotNull());
    }

    @Configuration
    @EnableAutoConfiguration
    static class AppConfigWithoutWebClientBean {

    }

    @HttpClient("todo-client")
    interface TodoClient {
        @GetExchange
        void get();
    }
}
