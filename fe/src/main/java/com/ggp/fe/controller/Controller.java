package com.ggp.fe.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class Controller {

    private static final String TRUSTED_SERVER = "https://localhost:8443";
    private static final String NOT_TRUSTED_SERVER = "https://api.publicapis.org";

    private final RestTemplate restTemplate;

    /**
     * For correct work, please run 'application' module!!
     * Demonstrate correct work, when we send request to "trusted" server.
     * @Return must return string "Hello Bob".
     */
    @GetMapping("/send")
    public String testTsl() {
        return restTemplate.exchange(TRUSTED_SERVER + "/api", HttpMethod.GET, null, String.class).getBody();
    }

    /**
     * Demonstrate incorrect work, when we sent request to NOT "trusted" server.
     * @return throws sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
     */
    @GetMapping("/test")
    public String test2() {
        return restTemplate.exchange(NOT_TRUSTED_SERVER + "/entries", HttpMethod.GET, null, String.class).getBody();
    }


}
