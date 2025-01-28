package com.hotel.appHotel.controller;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/dni")
public class DniController {

    private final String API_URL = "https://api.apis.net.pe/v1/dni";
    private final String API_TOKEN = "apis-token-12861.3HGx9vtDbTAztf6ugI3Mdumy8nq6jHK7";

    @GetMapping
    public ResponseEntity<?> getDniInfo(@RequestParam("numero") String numero) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + API_TOKEN);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                API_URL + "?numero=" + numero,
                HttpMethod.GET,
                entity,
                String.class
            );
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al consultar la API: " + e.getMessage());
        }
    }
}

