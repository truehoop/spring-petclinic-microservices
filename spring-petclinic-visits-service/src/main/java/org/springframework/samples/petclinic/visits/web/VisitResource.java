/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.visits.web;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.visits.model.Visit;
import org.springframework.samples.petclinic.visits.model.VisitRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 * @author Maciej Szarlinski
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@Timed("petclinic.visit")
class VisitResource {

    private final VisitRepository visitRepository;
    private final TestService testService;
    private final VetServiceClient vetService;

    @PostMapping("owners/*/pets/{petId}/visits")
    @ResponseStatus(HttpStatus.CREATED)
   public Visit create(
        @Valid @RequestBody Visit visit,
        @PathVariable("petId") int petId) {

        visit.setPetId(petId);
        log.info("Saving visit {}", visit);
        return visitRepository.save(visit);
    }

   @GetMapping("owners/*/pets/{petId}/visits")
   public List<Visit> visits(@PathVariable("petId") int petId) {
    	return visitRepository.findByPetId(petId);
    }
   
   @GetMapping("/fail")
   public Mono<String> getFail() {
    	return testService.getFailHello();
    }
   
   @GetMapping("/success")
   public Mono<String> getSuccess() {
    	return testService.getSuccessHello();
    }

   @GetMapping("pets/visits")
   public Visits visitsMultiGet(@RequestParam("petId") List<Integer> petIds) {
        final List<Visit> byPetIdIn = testService.getPets(petIds);
        log.info("visit is comming~");
//        Mono<String> test = vetService.getVets();
//        Mono<String> test2 = testService.getDataFromRemoteServer();
//        
        Mono<String> test = null;
        if(petIds.get(0) == 1)
        	test = testService.getDataFromRemoteServerSuccess();
        else if(petIds.get(0) == 2)
        	test = testService.getDataFromRemoteServerFail();
        else if(petIds.get(0) == 3)
        	test = testService.getFailHello();
        
        test.subscribe(result -> {
	          log.info("{}", result);
	      }, e -> {
	        log.info("{}", e.getMessage());
	      });
        
    	return new Visits(byPetIdIn);
    }

    @Value
    static class Visits {
        List<Visit> items;
    }
}
