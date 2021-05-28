package org.springframework.samples.petclinic.visits.web;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class VetServiceClient {

	// Could be changed for testing purpose
    private String hostname = "http://vets-service:8083/";

    private final WebClient.Builder webClientBuilder;

    public Mono<String> getVets() {
        return webClientBuilder.build()
            .get()
            .uri(hostname + "vets")
            .retrieve()
            .bodyToMono(String.class);
    }

    
    void setHostname(String hostname) {
        this.hostname = hostname;
    }
}
