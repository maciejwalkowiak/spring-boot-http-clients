package com.maciejwalkowiak.spring.http.registration;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.maciejwalkowiak.spring.http.annotation.EnableHttpClients;
import com.maciejwalkowiak.spring.http.annotation.HttpClient;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * Registers bean definition for HTTP clients annotated with {@link HttpClient}.
 *
 * @author Maciej Walkowiak
 */
public abstract class AbstractHttpClientsRegistrar implements ImportBeanDefinitionRegistrar, BeanFactoryAware {

    @Nullable
    protected BeanFactory beanFactory;

    protected void registerClients(Iterable<HttpClientCandidate> clientCandidates, BeanDefinitionRegistry registry) {
        for (HttpClientCandidate clientCandidate : clientCandidates) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(HttpClientFactoryBean.class);
            builder.addPropertyValue("clazz", clientCandidate.clazz());
            builder.addPropertyValue("clientName", clientCandidate.name());
            registry.registerBeanDefinition(clientCandidate.beanName(), builder.getBeanDefinition());
        }
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    protected record HttpClientCandidate(String name, Class<?> clazz) {
        String beanName() {
            return name + ".HttpClient";
        }
    }
}
