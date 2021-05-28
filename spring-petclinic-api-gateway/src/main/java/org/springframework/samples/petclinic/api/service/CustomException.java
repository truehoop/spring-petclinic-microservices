package org.springframework.samples.petclinic.api.service;

public class CustomException extends Exception {

	  public CustomException(String message) {
	    super(message);
	  }
}
