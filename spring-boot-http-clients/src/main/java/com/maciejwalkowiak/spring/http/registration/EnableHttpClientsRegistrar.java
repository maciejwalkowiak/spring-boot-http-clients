package com.maciejwalkowiak.spring.http.registration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.maciejwalkowiak.spring.http.annotation.EnableHttpClients;
import com.maciejwalkowiak.spring.http.annotation.HttpClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

/**
 * Registers bean definition for HTTP clients annotated with {@link HttpClient}.
 *
 * Meant to be used only with {@link EnableHttpClients}.
 *
 * @author Maciej Walkowiak
 */
public class EnableHttpClientsRegistrar extends AbstractHttpClientsRegistrar {
    private static final Log LOGGER = LogFactory.getLog(EnableHttpClientsRegistrar.class);

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry,
            BeanNameGenerator importBeanNameGenerator) {
        LOGGER.info("Registering clients set as an attribute of @EnableHttpClients annotation");

        registerClients(resolveClientsSetAsAttribute(importingClassMetadata), registry);
    }

    private List<HttpClientCandidate> resolveClientsSetAsAttribute(AnnotationMetadata importingClassMetadata) {
        Map<String, Object> annotationAttributes = importingClassMetadata.getAnnotationAttributes(EnableHttpClients.class.getName());
        if (annotationAttributes != null) {
            Class<?>[] clients = (Class<?>[]) annotationAttributes.get("clients");
            List<HttpClientCandidate> candidates = Arrays.stream(clients)
                    .map(it -> {
                        HttpClient httpClientAnnotation = AnnotationUtils.findAnnotation(it, HttpClient.class);
                        Assert.state(httpClientAnnotation != null, "@HttpClient annotation not found on class " + it);
                        return new HttpClientCandidate(httpClientAnnotation.value(), it);
                    }).toList();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Found " + candidates.size() + " candidates: " + candidates);
            }
            return candidates;
        } else {
            LOGGER.warn("@EnableHttpClients annotation not found");
            return Collections.emptyList();
        }
    }
}
