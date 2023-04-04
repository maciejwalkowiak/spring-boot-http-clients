package com.maciejwalkowiak.spring.http.registration;

import java.util.Map;

import com.maciejwalkowiak.spring.http.WebClientsProperties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * Factory bean creating instances of Spring HTTP Interface implementing {@link #clazz} using the underlying instance of {@link WebClient} given by {@link #clientName}.
 *
 * @author Maciej Walkowiak
 */
public class HttpClientFactoryBean implements FactoryBean<Object>, ApplicationContextAware {

    @Nullable
    private Class<?> clazz;

    @Nullable
    private String clientName;

    @Nullable
    private ApplicationContext applicationContext;

    @Override
    public Object getObject() throws Exception {
        Assert.state(applicationContext != null, "applicationContext cannot be null");
        Assert.state(clazz != null, "clazz cannot be null");
        Assert.state(clientName != null, "clientName cannot be null");

        Map<String, WebClient> webClients = applicationContext.getBeansOfType(WebClient.class);

        String beanName = clientName + ".WebClient";
        WebClient webClient = webClients.get(beanName);

        Assert.state(webClient != null, "WebClient bean with name = " + beanName + " not found");

        WebClientAdapter clientAdapter = WebClientAdapter.forClient(webClient);
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory.builder(clientAdapter).build();
        return httpServiceProxyFactory.createClient(clazz);
    }

    @Override
    public Class<?> getObjectType() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
