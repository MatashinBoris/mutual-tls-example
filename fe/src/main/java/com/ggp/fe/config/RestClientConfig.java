package com.ggp.fe.config;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;

@Configuration
@RequiredArgsConstructor
public class RestClientConfig {
    private static final String TLS_PROTOCOL = "TLS";
    @Value("${tsl.key-store.url}")
    private String keyStorePath;
    @Value("${tsl.key-store.password}")
    private String keyStorePassword;
    @Value("${tsl.key-store.server-certificate.password}")
    private String clientCertificatePassword;
    @Value("${tsl.trust-store.url}")
    private String trustStorePath;
    @Value("${tsl.trust-store.password}")
    private String trustStorePassword;
    private final ResourceLoader resourceLoader;

    @Bean
    @SneakyThrows
    public RestTemplate restTemplate() {

        SSLContext sslContext = SSLContextBuilder.create()
                .setProtocol(TLS_PROTOCOL)
                .loadKeyMaterial(resourceLoader.getResource(keyStorePath).getFile(), keyStorePassword.toCharArray(), clientCertificatePassword.toCharArray())
                .loadTrustMaterial(resourceLoader.getResource(trustStorePath).getFile(), trustStorePassword.toCharArray())
                .build();

        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(new SSLConnectionSocketFactory(sslContext))
                .build();

        return new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
    }

}
