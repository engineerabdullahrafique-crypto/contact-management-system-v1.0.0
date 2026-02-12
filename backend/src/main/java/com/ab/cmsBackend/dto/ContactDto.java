package com.ab.cmsBackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ContactDto {
    private Long id;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    private String title;
    private String emailWork;
    private String emailPersonal;
    private String phoneWork;
    private String phoneHome;
    private String phonePersonal;

    private Long userId; // This will be set from logged-in user, not from frontend
}