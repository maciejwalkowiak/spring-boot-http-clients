package com.maciejwalkowiak.spring.http.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.web.reactive.function.client.WebClient;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE})
@Documented
public @interface HttpClient {

    /**
     * Name of the {@link WebClient} bean to use with http client
     * @return bean name
     */
    String value();
}
