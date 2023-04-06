package com.maciejwalkowiak.spring.http.registration;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Registers bean definitions for {@link WebClient}s defined in Spring {@link Environment} under prefix {@code "http.clients}.
 *
 * @author Maciej Walkowiak
 * @author Tigran Babloyan
 */
public class WebClientsRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {
    private static final String PREFIX = "http.clients";

    private Environment environment;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry,
            BeanNameGenerator importBeanNameGenerator) {
        if (!(environment instanceof ConfigurableEnvironment)) {
            return;
        }
        for (String clientName : resolveClientNames()) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(WebClientFactoryBean.class);
            builder.addPropertyValue("name", clientName);
            registry.registerBeanDefinition(clientName + ".WebClient", builder.getBeanDefinition());
        }
    }

    private Set<String> resolveClientNames() {
        ConfigurableEnvironment env = (ConfigurableEnvironment) environment;
        return env.getPropertySources().stream()
                .flatMap(WebClientsRegistrar::resolvePropertyNames)
                .filter(it -> it.startsWith(PREFIX))
                .map(it -> it.substring((PREFIX + ".").length()))
                .map(it -> it.substring(0, it.indexOf(".")))
                .collect(Collectors.toSet());
    }

    private static Stream<String> resolvePropertyNames(PropertySource<?> it) {
        if (it instanceof EnumerablePropertySource<?>) {
            return Arrays.stream(((EnumerablePropertySource<?>) it).getPropertyNames());
        } else {
            return Stream.empty();
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
