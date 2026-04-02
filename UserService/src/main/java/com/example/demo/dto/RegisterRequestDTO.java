package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RegisterRequestDTO {
	@NotBlank
	private String userName;
	@Email
	@NotBlank
	private String email;
	@Pattern(
	        regexp = "^\\+\\d{1,4}$",
	        message = "Country code must start with + and contain 1 to 4 digits (e.g., +91)"
	    )
	    private String countryCode;

	    @Pattern(
	        regexp = "^[6-9]\\d{9}$",
	        message = "Phone number must be 10 digits and start with 6-9"
	    )
	    private String phoneNumber;
	@Pattern(
	        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$",
	        message = "Password must be 8+ chars with uppercase, lowercase, number, and special character"
	    )
	private String password;
}
