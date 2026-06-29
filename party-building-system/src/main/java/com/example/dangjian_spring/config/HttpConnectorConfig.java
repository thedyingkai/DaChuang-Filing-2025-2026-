package com.example.dangjian_spring.config;

import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 追加一个纯 HTTP 端口，专供同机 Dify 容器通过 host.docker.internal 调用。
 * 该端口仅监听 127.0.0.1，外部网络无法访问，不影响现有 8081 HTTPS。
 */
@Configuration
public class HttpConnectorConfig {

    @Value("${server.http.port:8082}")
    private int httpPort;

    @Value("${server.http.address:127.0.0.1}")
    private String httpAddress;

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> httpConnectorCustomizer() {
        return factory -> {
            Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
            connector.setPort(httpPort);
            connector.setSecure(false);
            connector.setScheme("http");
            connector.setProperty("address", httpAddress);
            factory.addAdditionalTomcatConnectors(connector);
        };
    }
}