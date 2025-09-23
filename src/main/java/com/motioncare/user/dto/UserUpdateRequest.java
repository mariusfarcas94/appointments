package com.motioncare.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    
    private String firstName;
    private String lastName;
    private Boolean enabled;
}
