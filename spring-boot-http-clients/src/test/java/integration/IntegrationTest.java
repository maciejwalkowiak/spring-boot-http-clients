package integration;

import com.maciejwalkowiak.spring.http.annotation.HttpClient;
import com.maciejwalkowiak.spring.http.autoconfigure.HttpClientsAutoConfiguration;
import com.maciejwalkowiak.spring.http.autoconfigure.WebClientsAutoConfiguration;
import okhttp3.Cookie;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration;
import org.springframework.boot.context.annotation.UserConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.service.annotation.GetExchange;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

public class IntegrationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(HttpClientsAutoConfiguration.class, WebClientsAutoConfiguration.class, WebClientAutoConfiguration.class));

    private MockWebServer server;


    @BeforeEach
    void setUp() {
        this.server = new MockWebServer();
    }

    @SuppressWarnings("ConstantConditions")
    @AfterEach
    void shutdown() throws IOException {
        if (this.server != null) {
            this.server.shutdown();
        }
    }

    @Test
    void createsWebClientsFromProperties() {
        contextRunner
                .withConfiguration(UserConfigurations.of(AppConfigWithoutWebClientBean.class))
                .withPropertyValues("http.clients.todo-client.url=http://foo.com/")
                .run(ctx -> assertThat(ctx.getBean(TodoClient.class)).isNotNull());
    }

    @Test
    void createsWebClientsFromPropertiesWithHeaders() {
        prepareResponse(response -> response.setResponseCode(200).setBody("Hello Http Clients!"));
        String baseUrl = this.server.url("/").toString();
        contextRunner
                .withSystemProperties("dynamicHeader:dynamicHeaderValue")
                .withConfiguration(UserConfigurations.of(AppConfigWithoutWebClientBean.class))
                .withPropertyValues("http.clients.todo-client.url=%s".formatted(baseUrl),
                        "http.clients.todo-client.headers.staticHeader=staticHeaderValue",
                        "http.clients.todo-client.headers.dynamicHeader=${dynamicHeader}")
                .run(ctx -> {
                    TodoClient todoClient = ctx.getBean(TodoClient.class);
                    todoClient.get();

                    RecordedRequest request = this.server.takeRequest();
                    assertThat(request.getHeaders().get("staticHeader")).isEqualTo("staticHeaderValue");
                    assertThat(request.getHeaders().get("dynamicHeader")).isEqualTo("dynamicHeaderValue");
                });
    }

    @Test
    void createsWebClientsFromPropertiesWithCookies() {
        prepareResponse(response -> response.setResponseCode(200).setBody("Hello Http Clients!"));
        String baseUrl = this.server.url("/").toString();
        contextRunner
                .withSystemProperties("dynamicCookie:dynamicCookieValue")
                .withConfiguration(UserConfigurations.of(AppConfigWithoutWebClientBean.class))
                .withPropertyValues("http.clients.todo-client.url=%s".formatted(baseUrl),
                        "http.clients.todo-client.cookies.staticCookie=staticCookieValue",
                        "http.clients.todo-client.cookies.dynamicCookie=${dynamicCookie}")
                .run(ctx -> {
                    TodoClient todoClient = ctx.getBean(TodoClient.class);
                    todoClient.get();

                    RecordedRequest request = this.server.takeRequest();
                    assertThat(request.getHeaders().get("Cookie")).isEqualTo("staticCookie=staticCookieValue;dynamicCookie=dynamicCookieValue");
                });
    }

    @Test
    void createsWebClientsFromPropertiesWithFilter() {
        prepareResponse(response -> response.setResponseCode(200).setBody("Hello Http Clients!"));
        String baseUrl = this.server.url("/").toString();
        contextRunner
                .withConfiguration(UserConfigurations.of(AppConfigWithoutWebClientBean.class))
                .withPropertyValues("http.clients.todo-client.url=%s".formatted(baseUrl),
                        "http.clients.todo-client.filters=filterFunction1,filterFunction2")
                .run(ctx -> {
                    TodoClient todoClient = ctx.getBean(TodoClient.class);
                    todoClient.get();

                    RecordedRequest request = this.server.takeRequest();
                    assertThat(request.getHeaders().get("Function1")).isEqualTo("Function1");
                    assertThat(request.getHeaders().get("Function2")).isEqualTo("Function2");
                });
    }

    private void prepareResponse(Consumer<MockResponse> consumer) {
        MockResponse response = new MockResponse();
        consumer.accept(response);
        this.server.enqueue(response);
    }

    @Configuration
    @EnableAutoConfiguration
    static class AppConfigWithoutWebClientBean {
        @Bean
        ExchangeFilterFunction filterFunction1(){
            return ExchangeFilterFunction.ofRequestProcessor(
                    req -> Mono.just(ClientRequest.from(req)
                            .header("Function1", "Function1")
                            .build())
            );
        }

        @Bean
        ExchangeFilterFunction filterFunction2(){
            return ExchangeFilterFunction.ofRequestProcessor(
                    req -> Mono.just(ClientRequest.from(req)
                            .header("Function2", "Function2")
                            .build())
            );
        }
    }

    @HttpClient("todo-client")
    interface TodoClient {
        @GetExchange
        void get();
    }
}
