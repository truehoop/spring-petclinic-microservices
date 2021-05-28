package org.springframework.samples.petclinic.api.boundary.web;

import org.springframework.samples.petclinic.api.service.TestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test")
public class TestController {

	private final TestService testService;

	  @GetMapping("fail")
	  public Mono<String> getFailHello() {
	    return testService.getFailHello();
	  }

	  @GetMapping("success")
	  public Mono<String> getSuccessHello() {
	    return testService.getSuccessHello();
	  }

	  @GetMapping("data")
	  public Mono<String> getDataFromRemoteServer() {
	    return testService.getDataFromRemoteServer();
	  }

}
