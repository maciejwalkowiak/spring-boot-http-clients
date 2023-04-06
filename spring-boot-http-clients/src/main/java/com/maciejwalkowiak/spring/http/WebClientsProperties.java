package com.maciejwalkowiak.spring.http;

import java.util.List;
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
        
        private List<String> filters;
        
        private Map<String, String> headers;

        private Map<String, String> cookies;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Map<String, String> getHeaders() {
            return headers;
        }

        public void setHeaders(Map<String, String> headers) {
            this.headers = headers;
        }

        public List<String> getFilters() {
            return filters;
        }

        public void setFilters(List<String> filters) {
            this.filters = filters;
        }

        public Map<String, String> getCookies() {
            return cookies;
        }

        public void setCookies(Map<String, String> cookies) {
            this.cookies = cookies;
        }
    }
}
