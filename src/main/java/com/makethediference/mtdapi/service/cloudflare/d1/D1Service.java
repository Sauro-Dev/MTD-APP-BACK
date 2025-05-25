package com.makethediference.mtdapi.service.cloudflare.d1;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class D1Service {

    private final RestTemplate restTemplate;
    private final D1Config d1Config;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public D1Service(RestTemplate restTemplate, D1Config d1Config) {
        this.restTemplate = restTemplate;
        this.d1Config = d1Config;
    }

    private <T> List<T> parseResponse(String jsonResponse, Class<T> clazz) {
        if (jsonResponse == null || jsonResponse.isEmpty()) {
            return Collections.emptyList();
        }

        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode results = root.path("result").path("results");

            // Verificar si "results" está vacío
            if (results.isMissingNode() || results.isEmpty()) {
                return Collections.emptyList();
            }

            return objectMapper.readValue(
                    results.toString(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, clazz)
            );
        } catch (Exception e) {
            throw new RuntimeException("Error al parsear respuesta de D1: " + e.getMessage(), e);
        }
    }

    public ResponseEntity<String> executeQuery(String sql, List<Object> params) {
        String url = d1Config.getD1ApiUrl();
        HttpHeaders headers = d1Config.getHeaders();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("sql", sql);
        requestBody.put("params", params);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        // Imprimir la respuesta para depuración
        System.out.println("Response: " + response.getBody());

        return response;
    }

    // Método para operaciones SELECT (ejemplo)
    public <T> List<T> queryForList(String sql, Class<T> clazz, List<Object> params) {
        ResponseEntity<String> response = executeQuery(sql, params);
        return parseResponse(response.getBody(), clazz);
    }
}