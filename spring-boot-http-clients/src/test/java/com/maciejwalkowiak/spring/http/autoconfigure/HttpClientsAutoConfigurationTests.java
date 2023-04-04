package com.maciejwalkowiak.spring.http.autoconfigure;

import app.UserClient;
import com.maciejwalkowiak.spring.http.annotation.EnableHttpClients;
import com.maciejwalkowiak.spring.http.annotation.HttpClient;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.annotation.UserConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.service.annotation.GetExchange;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpClientsAutoConfigurationTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(HttpClientsAutoConfiguration.class));


    @Test
    void foo() {
        this.contextRunner
                .withConfiguration(UserConfigurations.of(AppConfigWithWebClientBean.class))
                .run(ctx -> assertThat(ctx.getBean(TodoClient.class)).isNotNull());
    }

    @Test
    void foo2() {
        this.contextRunner
                .withConfiguration(UserConfigurations.of(AppConfigWithoutWebClientBean.class))
                .run(ctx -> assertThatThrownBy(() -> ctx.getBean(TodoClient.class))
                        .isInstanceOf(BeanCreationException.class));
    }

    @Test
    void foo3() {
        this.contextRunner
                .withConfiguration(UserConfigurations.of(AppConfigWithBeanOutsideOfMainPackage.class))
                .run(ctx -> assertThat(ctx.getBean(UserClient.class)).isNotNull());
    }

    @Configuration
    @EnableAutoConfiguration(exclude = WebClientsAutoConfiguration.class)
    static class AppConfigWithWebClientBean {

        @Bean(name = "todo-client.WebClient")
        WebClient webClient() {
            return WebClient.builder().build();
        }
    }

    @Configuration
    @EnableAutoConfiguration(exclude = WebClientsAutoConfiguration.class)
    static class AppConfigWithoutWebClientBean {
    }

    @Configuration
    @EnableAutoConfiguration(exclude = WebClientsAutoConfiguration.class)
    @EnableHttpClients(clients = UserClient.class)
    static class AppConfigWithBeanOutsideOfMainPackage {

        @Bean(name = "user-client.WebClient")
        WebClient webClient() {
            return WebClient.builder().build();
        }
    }

    @HttpClient("todo-client")
    interface TodoClient {
        @GetExchange
        void get();
    }

}
