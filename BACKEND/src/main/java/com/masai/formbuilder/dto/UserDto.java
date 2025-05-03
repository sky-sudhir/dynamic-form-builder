package com.masai.formbuilder.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String firstName;
    private String lastName;

    @Email(message = "Email Format is not correct")
    private String email;
    
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
    private String role;
    
    private String mobileNumber;
}
