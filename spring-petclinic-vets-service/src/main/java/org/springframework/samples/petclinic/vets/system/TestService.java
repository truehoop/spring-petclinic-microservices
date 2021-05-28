package org.springframework.samples.petclinic.vets.system;

import java.util.List;

import org.springframework.samples.petclinic.vets.model.Vet;
import org.springframework.samples.petclinic.vets.model.VetRepository;
import org.springframework.stereotype.Service;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class TestService {
	
	private static final String TEST_CIRCUIT_BREAKER = "vetCircuitBreaker";
	private final VetRepository vetRepository;

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
	  @CircuitBreaker(name = TEST_CIRCUIT_BREAKER, fallbackMethod = "listfallback")
	  public List<Vet> getVets() {
		  log.info("call vets list");
	    return vetRepository.findAll();
	  }

	  
	  public Mono<String> fallback(CustomException e) {
		  log.info("fallback");
	    return Mono.just("fallback");
	  }

	  public Mono<String> fallback(CallNotPermittedException e) {
		  log.info("CallNotPermittedException");
	    return Mono.just("CallNotPermittedException");
	  }
	  
	  public List<Vet> listfallback(CustomException e) {
		  log.info("listfallback");
	    return null;
	  }

	  public List<Vet> listfallback(CallNotPermittedException e) {
		  log.info("listCallNotPermittedException");
	    return null;
	  }

}
