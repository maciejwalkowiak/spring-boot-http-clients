package com.maciejwalkowiak.spring.http.registration;

import com.maciejwalkowiak.spring.http.annotation.HttpClient;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.lang.Nullable;

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
