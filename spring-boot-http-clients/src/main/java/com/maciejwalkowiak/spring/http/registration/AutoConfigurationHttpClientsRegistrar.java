package com.maciejwalkowiak.spring.http.registration;

import java.util.Collections;
import java.util.List;

import com.maciejwalkowiak.spring.http.annotation.HttpClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * Registers bean definition for HTTP clients annotated with {@link HttpClient} by looking into the packages of classes
 * annotated with {@link EnableAutoConfiguration} or {@link SpringBootApplication}.
 * If such class is not found, does not register anything.
 *
 * @author Maciej Walkowiak
 */
public class AutoConfigurationHttpClientsRegistrar extends AbstractHttpClientsRegistrar {
    private static final Log LOGGER = LogFactory.getLog(AutoConfigurationHttpClientsRegistrar.class);

    @Override public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry,
            BeanNameGenerator importBeanNameGenerator) {
        LOGGER.info("Registering clients discovered in the main package");

        registerClients(resolveClientsInMainPackages(), registry);
    }

    private List<HttpClientCandidate> resolveClientsInMainPackages() {
        Assert.state(beanFactory != null, "beanFactory cannot be null");

        List<String> mainPackages = resolveMainPackages();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Resolved main packages: " + mainPackages);
        }

        if (!mainPackages.isEmpty()) {
            ClassPathScanningCandidateComponentProvider scanner = getScanner();
            scanner.addIncludeFilter(new AnnotationTypeFilter(HttpClient.class));

            List<HttpClientCandidate> candidates = mainPackages.stream()
                    .flatMap(it -> scanner.findCandidateComponents(it).stream())
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

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Found " + candidates.size() + " candidates: " + candidates);
            }

            return candidates;
        }
        return Collections.emptyList();
    }

    private List<String> resolveMainPackages() {
        try {
            return AutoConfigurationPackages.get(beanFactory);
        } catch (IllegalStateException e) {
            LOGGER.debug("Main package not found");
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
