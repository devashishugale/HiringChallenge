package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class ChallengeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChallengeApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    public CommandLineRunner run(RestTemplate restTemplate) throws Exception {
        return args -> {
           
            String myName = "Devashish Surendra Ugale"; 
            String myRegNo = "250850120065"; 
            String myEmail = "devashishugale2019@gmail.com";
            
            String generateUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

            System.out.println(">>> 1. STARTING REGISTRATION...");

            Map<String, String> regMap = new HashMap<>();
            regMap.put("name", myName);
            regMap.put("regNo", myRegNo);
            regMap.put("email", myEmail);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> regEntity = new HttpEntity<>(regMap, headers);

            try {
              
                ResponseEntity<Map> response = restTemplate.postForEntity(generateUrl, regEntity, Map.class);
                Map<String, Object> body = response.getBody();

              
                String accessToken = (String) body.get("accessToken"); 
                String webhookUrl = (String) body.get("webhook"); 

                System.out.println(">>> Token Received: " + accessToken);
                System.out.println(">>> Webhook URL: " + webhookUrl);

               
                String finalSqlQuery = "SELECT\r\n"
                		+ "    D.DEPARTMENT_NAME,\r\n"
                		+ "    MAX(RE.SALARY),\r\n"
                		+ "    RE.FIRST_NAME || ' ' || RE.LAST_NAME AS EMPLOYEE_NAME,\r\n"
                		+ "    RE.AGE\r\n"
                		+ "FROM\r\n"
                		+ "PAYMET P  JOIN\r\n"
                		+ "    EMPLOYEE RE\r\n"
                		+ "JOIN\r\n"
                		+ "    DEPARTMENT D ON RE.DEPARTMENT_ID = D.DEPARTMENT_ID\r\n"
                		+ "WHERE\r\n"
                		+ "  EXTRACT(DAY FROM P.PAYMENT_TIME) != 1 -- or DAY(P.PAYMENT_TIME) != 1\r\n"
                		+ "ORDER BY\r\n"
                		+ "    D.DEPARTMENT_NAME;"; 

              
                System.out.println(">>> 2. SUBMITTING SOLUTION...");
                
                HttpHeaders authHeaders = new HttpHeaders();
                authHeaders.setContentType(MediaType.APPLICATION_JSON);
                authHeaders.set("Authorization", accessToken); 

                Map<String, String> ansMap = new HashMap<>();
                ansMap.put("finalQuery", finalSqlQuery);

                HttpEntity<Map<String, String>> ansEntity = new HttpEntity<>(ansMap, authHeaders);

              
                ResponseEntity<String> result = restTemplate.postForEntity(webhookUrl, ansEntity, String.class);
                
                System.out.println(">>> FINAL SERVER RESPONSE: " + result.getStatusCode());
                System.out.println(">>> BODY: " + result.getBody());

            } catch (Exception e) {
                System.err.println(">>> ERROR: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }
}