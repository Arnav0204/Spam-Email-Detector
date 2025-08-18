package org.example.Controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.Console;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

@RestController
@RequestMapping("/email")
public class EmailController {
    private HttpClient client;

    @Value("${app.api-url}")
    private String url;
    public EmailController(HttpClient client){
        this.client=client;
    }

    @PostMapping("/predict")
    public ResponseEntity<Object> predict(@RequestBody Map<String,String> requestBody,
                                          Authentication authentication){
        try {

            String username = (String) authentication.getPrincipal();
            System.out.println("authenticated user : " + username);
            String text = requestBody.get("text");

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonPayload = objectMapper.writeValueAsString(Map.of("text", text));
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "/predict"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            // Parse the JSON response
            JsonNode jsonResponse = objectMapper.readTree(response.body());

            // Optionally extract fields
            int label = jsonResponse.get("label").asInt();
            String prediction = jsonResponse.get("prediction").asText();
            double probability = jsonResponse.get("probability").asDouble();

            // Return as is, or wrap in your own object
            return ResponseEntity.status(response.statusCode()).body(jsonResponse);

        } catch (Exception e) {
            System.out.println("Message : "+ e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
