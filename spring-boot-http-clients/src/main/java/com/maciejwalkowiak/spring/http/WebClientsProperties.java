package com.maciejwalkowiak.spring.http;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("http")
public class WebClientsProperties {
    private Map<String, WebClientConfig> clients;

    public Map<String, WebClientConfig> getClients() {
        return clients;
    }

    public void setClients(
            Map<String, WebClientConfig> clients) {
        this.clients = clients;
    }

    public static class WebClientConfig {
        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
