package com.maciejwalkowiak.spring.http.registration;

import java.util.Collections;
import java.util.List;

import com.maciejwalkowiak.spring.http.annotation.HttpClient;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * Registers bean definition for HTTP clients annotated with {@link HttpClient}.
 *
 * @author Maciej Walkowiak
 */
public class AutoConfigurationHttpClientsRegistrar extends AbstractHttpClientsRegistrar {

    @Override public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry,
            BeanNameGenerator importBeanNameGenerator) {
        registerClients(resolveClientsInMainPackages(), registry);
    }

    private List<HttpClientCandidate> resolveClientsInMainPackages() {
        Assert.state(beanFactory != null, "beanFactory cannot be null");

        List<String> mainPackages = resolveMainPackages();

        if (!mainPackages.isEmpty()) {
            ClassPathScanningCandidateComponentProvider scanner = getScanner();
            scanner.addIncludeFilter(new AnnotationTypeFilter(HttpClient.class));

            return mainPackages.stream().flatMap(it -> scanner.findCandidateComponents(it).stream())
                    .filter(it -> it.getBeanClassName() != null)
                    .map(it -> {
                        try {
                            Class<?> clazz = ClassUtils.forName(it.getBeanClassName(), null);
                            HttpClient httpClientAnnotation = AnnotationUtils.findAnnotation(clazz, HttpClient.class);

                            Assert.state(httpClientAnnotation != null, "@HttpClient annotation not found on class " + it);
                            return new HttpClientCandidate(httpClientAnnotation.value(), clazz);
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }).toList();
        }
        return Collections.emptyList();
    }

    private List<String> resolveMainPackages() {
        try {
            return AutoConfigurationPackages.get(beanFactory);
        } catch (IllegalStateException e) {
            return Collections.emptyList();
        }
    }

    protected ClassPathScanningCandidateComponentProvider getScanner() {
        return new ClassPathScanningCandidateComponentProvider(false) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                boolean isCandidate = false;
                if (beanDefinition.getMetadata().isIndependent()) {
                    if (!beanDefinition.getMetadata().isAnnotation()) {
                        isCandidate = true;
                    }
                }
                return isCandidate;
            }
        };
    }
}
