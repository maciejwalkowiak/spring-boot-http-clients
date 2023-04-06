package com.maciejwalkowiak.spring.http.registration;

import com.maciejwalkowiak.spring.http.WebClientsProperties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Factory bean creating instances of {@link WebClient} from configuration provided through {@link WebClientsProperties}.
 *
 * @author Maciej Walkowiak
 * @author Tigran Babloyan
 */
public class WebClientFactoryBean  implements FactoryBean<Object>, ApplicationContextAware {
    @Nullable
    private ApplicationContext applicationContext;

    /**
     * WebClient name referring to the key in {@link WebClientsProperties#getClients()}.
     */
    @Nullable
    private String name;

    @Override
    public Object getObject() throws Exception {
        Assert.state(applicationContext != null, "applicationContext cannot be null");
        Assert.state(name != null, "name cannot be null");

        WebClientsProperties.WebClientConfig webClientConfig = applicationContext.getBean(WebClientsProperties.class)
                .getClients()
                .get(name);

        Assert.state(webClientConfig != null, "WebClient with name: " + name + " is not configured");

        WebClient.Builder builder = applicationContext.getBean(WebClient.Builder.class)
                .baseUrl(webClientConfig.getUrl());
        if(!CollectionUtils.isEmpty(webClientConfig.getFilters())){
            webClientConfig.getFilters().stream()
                    .map(filter -> applicationContext.getBean(filter, ExchangeFilterFunction.class))
                    .forEach(builder::filter);
        }

        if(!CollectionUtils.isEmpty(webClientConfig.getHeaders())){
            webClientConfig.getHeaders().forEach(builder::defaultHeader);
        }

        if(!CollectionUtils.isEmpty(webClientConfig.getCookies())){
            webClientConfig.getCookies().forEach(builder::defaultCookie);
        }

        return builder.build();
    }

    @Override
    public Class<?> getObjectType() {
        return WebClient.class;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void setName(String name) {
        this.name = name;
    }
}
