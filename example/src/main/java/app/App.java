package app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    ExchangeFilterFunction filterFunction(){
        return ExchangeFilterFunction.ofRequestProcessor(
                req -> Mono.just(ClientRequest.from(req)
                        .header("header2", "value2")
                        .build())
        );
    }
}

