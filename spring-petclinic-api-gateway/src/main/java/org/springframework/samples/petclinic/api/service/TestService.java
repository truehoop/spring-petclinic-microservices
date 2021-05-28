package org.springframework.samples.petclinic.api.service;

import java.util.Collections;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import reactor.core.publisher.Mono;

@Service
public class TestService {
	
	private static final String TEST_CIRCUIT_BREAKER = "testCircuitBreaker";

	  @Bulkhead(name = TEST_CIRCUIT_BREAKER)
	  @CircuitBreaker(name = TEST_CIRCUIT_BREAKER, fallbackMethod = "fallback")
	  public Mono<String> getFailHello() {
	    return Mono.error(new CustomException("CustomException"));
	  }

	  @RateLimiter(name = TEST_CIRCUIT_BREAKER)
	  @Bulkhead(name = TEST_CIRCUIT_BREAKER)
	  @CircuitBreaker(name = TEST_CIRCUIT_BREAKER, fallbackMethod = "fallback")
	  public Mono<String> getSuccessHello() {
	    return Mono.just("Hello");
	  }

	  @TimeLimiter(name = TEST_CIRCUIT_BREAKER)
	  @Retry(name = TEST_CIRCUIT_BREAKER)
	  @CircuitBreaker(name = TEST_CIRCUIT_BREAKER, fallbackMethod = "fallback")
	  public Mono<String> getDataFromRemoteServer() {
	    WebClient webClient = WebClient
	        .builder()
	        .baseUrl("http://visits-service:8082/pets/visits?petId=1")
	        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
	        .defaultUriVariables(Collections.singletonMap("url", "http://visits-service:8082/pets/visits?petId=1"))
	        .build();

	    return webClient
	        .method(HttpMethod.GET)
	        .uri("/")
	        .retrieve()
	        .bodyToMono(String.class);
	  }

	  public Mono<String> fallback(CustomException e) {
	    return Mono.just("fallback");
	  }

	  public Mono<String> fallback(CallNotPermittedException e) {
	    return Mono.just("CallNotPermittedException");
	  }

}
