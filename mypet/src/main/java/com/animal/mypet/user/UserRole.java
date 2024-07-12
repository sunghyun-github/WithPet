package com.animal.mypet.user;

import lombok.Getter;

@Getter
public enum UserRole {
	
	ADMIN("ADMIN"),
	PETMANAGER("PET_MANAGER"),
	USER("USER");
	
	UserRole(String value){
		this.value = value;
	};
	
	private String value;
	
}
