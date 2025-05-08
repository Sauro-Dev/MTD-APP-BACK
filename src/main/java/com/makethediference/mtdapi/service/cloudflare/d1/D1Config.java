package com.makethediference.mtdapi.service.cloudflare.d1;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

@Configuration
public class D1Config {

    @Value("${CLOUDFLARE_D1_ACCOUNT_ID}")
    private String accountId;

    @Value("${CLOUDFLARE_D1_DATABASE_ID}")
    private String databaseId;

    @Value("${CLOUDFLARE_D1_API_TOKEN}")
    private String apiToken;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public String getD1ApiUrl() {
        return String.format(
                "https://api.cloudflare.com/client/v4/accounts/%s/d1/database/%s/query",
                accountId,
                databaseId
        );
    }

    public HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}