package com.masai.formbuilder.dto;

import com.masai.formbuilder.model.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthenticationResponse  {
    private String token;
    
    private String firstName;
    private String lastName;
    private String mobileNumber;
    private String email;
    private String role;
    
    public static AuthenticationResponse mapToResponse(User user, String token) {
    	return AuthenticationResponse.builder()
    			.token(token)
    			.firstName(user.getFirstName())
    			.lastName(user.getLastName())
    			.mobileNumber(user.getMobileNumber())
    			.email(user.getEmail())
    			.role(user.getRole().toString())
    			.build();
    }   
}
