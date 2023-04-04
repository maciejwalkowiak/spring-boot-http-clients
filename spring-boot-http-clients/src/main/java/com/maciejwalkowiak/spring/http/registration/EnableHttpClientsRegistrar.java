package com.maciejwalkowiak.spring.http.registration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.maciejwalkowiak.spring.http.annotation.EnableHttpClients;
import com.maciejwalkowiak.spring.http.annotation.HttpClient;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

/**
 * Registers bean definition for HTTP clients annotated with {@link HttpClient}.
 *
 * @author Maciej Walkowiak
 */
public class EnableHttpClientsRegistrar extends AbstractHttpClientsRegistrar {


    @Override public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry,
            BeanNameGenerator importBeanNameGenerator) {

        registerClients(resolveClientsSetAsAttribute(importingClassMetadata), registry);
    }

    private List<HttpClientCandidate> resolveClientsSetAsAttribute(AnnotationMetadata importingClassMetadata) {
        Map<String, Object> annotationAttributes = importingClassMetadata.getAnnotationAttributes(EnableHttpClients.class.getName());
        if (annotationAttributes != null) {
            Class<?>[] clients = (Class<?>[]) annotationAttributes.get("clients");
            return Arrays.stream(clients)
                    .map(it -> {
                        HttpClient httpClientAnnotation = AnnotationUtils.findAnnotation(it, HttpClient.class);
                        Assert.state(httpClientAnnotation != null, "@HttpClient annotation not found on class " + it);
                        return new HttpClientCandidate(httpClientAnnotation.value(), it);
                    }).toList();
        } else {
            return Collections.emptyList();
        }
    }
}
