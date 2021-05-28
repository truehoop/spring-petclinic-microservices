package org.springframework.samples.petclinic.visits.web;

public class CustomException extends Exception {

	  public CustomException(String message) {
	    super(message);
	  }
}
