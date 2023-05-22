package com.ggp.fe.config;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;



import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.security.KeyStore;

@Configuration
@RequiredArgsConstructor
public class RestClientConfig {
    private static final String JKS_TYPE = "jks";
    private static final String TLS_PROTOCOL = "TLS";
    @Value("${tsl.key-store.url}")
    private String keyStoreUrl;
    @Value("${tsl.key-store.password}")
    private String keyStorePassword;
    @Value("${tsl.key-store.server-certificate.password}")
    private String serverCertificatePassword;
    @Value("${tsl.trust-store.url}")
    private String trustStoreUrl;
    @Value("${tsl.trust-store.password}")
    private String trustStorePassword;
    private final ResourceLoader resourceLoader;

    @Bean
    @SneakyThrows
    public RestTemplate restTemplate() {

        KeyStore clientStore = KeyStore.getInstance(JKS_TYPE);
        clientStore.load(resourceLoader.getResource(keyStoreUrl).getInputStream(),
                keyStorePassword.toCharArray());

        // Загрузка доверенного хранилища (trustStore) в формате JKS
        KeyStore trustStore = KeyStore.getInstance(JKS_TYPE);
        trustStore.load(resourceLoader.getResource(trustStoreUrl).getInputStream(),
                trustStorePassword.toCharArray());


        SSLContext sslContext = SSLContextBuilder.create()
                .setProtocol(TLS_PROTOCOL)
                .loadKeyMaterial(clientStore, serverCertificatePassword.toCharArray())
                .loadTrustMaterial(trustStore, new TrustSelfSignedStrategy())
                .build();

        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(new SSLConnectionSocketFactory(sslContext))
                .build();

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        return new RestTemplate(requestFactory);
    }
}
