package com.maciejwalkowiak.spring.http.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.maciejwalkowiak.spring.http.registration.WebClientsRegistrar;

import org.springframework.context.annotation.Import;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE})
@Documented
@Import({ WebClientsRegistrar.class })
public @interface EnableWebClients {
}
