package com.animal.mypet.payment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@Service
public class PaymentService {

    @Value("${iamport.api.key}")
    private String apiKey;

    @Value("${iamport.api.secret}")
    private String apiSecret;

    private final String API_URL = "https://api.iamport.kr";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String getAccessToken() throws Exception {
        String url = API_URL + "/users/getToken";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        String body = "{ \"imp_key\": \"" + apiKey + "\", \"imp_secret\": \"" + apiSecret + "\" }";
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        // JSON 응답에서 토큰 추출
        JsonNode jsonResponse = objectMapper.readTree(response.getBody());
        return jsonResponse.path("response").path("access_token").asText();
    }

    public boolean verifyPayment(String token, String impUid, double amount) {
        String url = API_URL + "/payments/verify/" + impUid;
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        headers.set("Content-Type", "application/json");

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);

        return validatePaymentResponse(response.getBody(), amount);
    }
    
    private boolean validatePaymentResponse(String responseBody, double amount) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            JsonNode response = jsonNode.path("response");
            double paidAmount = response.path("amount").asDouble();
            return paidAmount == amount;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
