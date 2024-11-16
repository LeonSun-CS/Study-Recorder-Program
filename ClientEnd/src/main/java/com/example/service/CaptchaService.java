package com.example.service;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class CaptchaService {

    /**
     * this helper method returns string 'success' or corresponding error messages based on the verification
     * status that Cloudflare returned
     */
    public String verify(String cfResponse) {
        String verifyUrl = "https://challenges.cloudflare.com/turnstile/v0/siteverify";
//        String secretKey = "2x0000000000000000000000000000000AA";
//        String secretKey = "1x0000000000000000000000000000000AA";
        String secretKey = "0x4AAAAAAAYlvIEDDWQ4aoAeipnpCVRgjwo";
        // prepare http entity
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("secret", secretKey);
        requestBody.add("response", cfResponse);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(verifyUrl, request, String.class);
        JSONObject jsonObject = new JSONObject(response.getBody());
        if (jsonObject.getBoolean("success")) {
            return "success";
        }
        return jsonObject.getJSONArray("error-codes").toString();
    }
}
