package com.example.transaction_service.service;
import com.example.transaction_service.exceptions.DownstreamException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

public class WebClientErrorHandler {

    public static Mono<? extends Throwable> handle(ClientResponse response) {
        return response.bodyToMono(String.class)
                .flatMap(body -> {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode json = mapper.readTree(body);

                        int status = json.has("status") ? json.get("status").asInt() : response.statusCode().value();
                        String error = json.has("error") ? json.get("error").asText() : "Error";
                        String message = json.has("message") ? json.get("message").asText() : body;

                        return Mono.error(new DownstreamException(message, response.statusCode(), error));
                    } catch (Exception e) {
                        return Mono.error(new DownstreamException(body, response.statusCode(), "Error"));
                    }
                });
    }
}


