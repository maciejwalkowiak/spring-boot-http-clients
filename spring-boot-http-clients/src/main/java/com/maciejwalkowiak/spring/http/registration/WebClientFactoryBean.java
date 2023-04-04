package com.maciejwalkowiak.spring.http.registration;

import com.maciejwalkowiak.spring.http.WebClientsProperties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Factory bean creating instances of {@link WebClient} from configuration provided through {@link WebClientsProperties}.
 *
 * @author Maciej Walkowiak
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

        WebClient.Builder builder = applicationContext.getBean(WebClient.Builder.class);

        return builder.baseUrl(webClientConfig.getUrl())
                .build();
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
