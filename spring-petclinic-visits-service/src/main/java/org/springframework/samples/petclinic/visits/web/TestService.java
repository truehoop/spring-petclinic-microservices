package org.springframework.samples.petclinic.visits.web;

import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.visits.model.Visit;
import org.springframework.samples.petclinic.visits.model.VisitRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class TestService {
	
	private final VisitRepository visitRepository;
	private static final String TEST_CIRCUIT_BREAKER = "visitCircuitBreaker";

	  @Bulkhead(name = TEST_CIRCUIT_BREAKER)
	  @CircuitBreaker(name = TEST_CIRCUIT_BREAKER, fallbackMethod = "fallback")
	  public Mono<String> getFailHello() {
		  log.info("call fail hello");
	    return Mono.error(new CustomException("CustomException"));
	  }

	  @RateLimiter(name = TEST_CIRCUIT_BREAKER)
	  @Bulkhead(name = TEST_CIRCUIT_BREAKER)
	  @CircuitBreaker(name = TEST_CIRCUIT_BREAKER, fallbackMethod = "fallback")
	  public Mono<String> getSuccessHello() {
		  log.info("call success hello");
	    return Mono.just("Hello");
	  }
	  @RateLimiter(name = TEST_CIRCUIT_BREAKER)
	  @Bulkhead(name = TEST_CIRCUIT_BREAKER)
	  @CircuitBreaker(name = TEST_CIRCUIT_BREAKER, fallbackMethod = "fallbackList")
	  public List<Visit> getPets(List<Integer> petIds) {
		  log.info("call visit list");
		  return visitRepository.findByPetIdIn(petIds);
	  }

	  @TimeLimiter(name = TEST_CIRCUIT_BREAKER)
	  @Retry(name = TEST_CIRCUIT_BREAKER)
	  @CircuitBreaker(name = TEST_CIRCUIT_BREAKER, fallbackMethod = "fallback")
	  public Mono<String> getDataFromRemoteServerFail() {
		  log.info("call vets fail hello");
	    WebClient webClient = WebClient
	        .builder()
	        .baseUrl("http://vets-service:8083/api/test/fail")
	        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
	        .defaultUriVariables(Collections.singletonMap("url", "http://vets-service:8083/api/test/fail"))
	        .build();
	    return webClient
	        .method(HttpMethod.GET)
	        .uri("/")
	        .retrieve()
	        .bodyToMono(String.class);
	  }
	  
	  @TimeLimiter(name = TEST_CIRCUIT_BREAKER)
	  @Retry(name = TEST_CIRCUIT_BREAKER)
	  @CircuitBreaker(name = TEST_CIRCUIT_BREAKER, fallbackMethod = "fallback")
	  public Mono<String> getDataFromRemoteServerSuccess() {
		  log.info("call vets success hello");
	    WebClient webClient = WebClient
	        .builder()
	        .baseUrl("http://vets-service:8083/api/test/success")
	        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
	        .defaultUriVariables(Collections.singletonMap("url", "http://vets-service:8083/api/test/success"))
	        .build();
	    return webClient
	        .method(HttpMethod.GET)
	        .uri("/")
	        .retrieve()
	        .bodyToMono(String.class);
	  }

	  public Mono<String> fallback(CustomException e) {
		  log.info("fallback");
		  return Mono.just("fallback");
	  }
	  
	  public List<Visit> fallbackList(CustomException e) {
		  log.info("list fallback");
		  return null;
	  }
	  
	  public Mono<String> fallback(CallNotPermittedException e) {
		  log.info("CallNotPermittedException");
		  return Mono.just("CallNotPermittedException");
	  }
	  
	  public List<Visit> fallbackList(CallNotPermittedException e) {
		  log.info("list CallNotPermittedException");
		  return null;
	  }

}
