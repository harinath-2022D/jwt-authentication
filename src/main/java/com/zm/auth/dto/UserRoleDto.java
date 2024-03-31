package com.zm.auth.dto;

import java.util.List;

import lombok.Data;

@Data
public class UserRoleDto {
	
	private String username;
	
	private List<String> roles;
}
