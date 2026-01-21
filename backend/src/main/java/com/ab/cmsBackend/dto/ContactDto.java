package com.ab.cmsBackend.dto;

import lombok.Data;

@Data
public class ContactDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String title;
    private String emailWork;
    private String emailPersonal;
    private String phoneWork;
    private String phoneHome;
    private String phonePersonal;
    private Long userId;
}