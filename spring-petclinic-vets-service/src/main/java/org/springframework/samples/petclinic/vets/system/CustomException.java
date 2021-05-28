package org.springframework.samples.petclinic.vets.system;

public class CustomException extends Exception {

	  public CustomException(String message) {
	    super(message);
	  }
}
