package com.example.demo.dto;

import lombok.Data;

@Data
public class LoginRequestDTO {
	private String identifier;
	private String password;
}
