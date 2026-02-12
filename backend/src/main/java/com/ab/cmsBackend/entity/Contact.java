package com.ab.cmsBackend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String title;
    private String emailWork;
    private String emailPersonal;
    private String phoneWork;
    private String phoneHome;
    private String phonePersonal;

    @ManyToOne
    private User user;
}
