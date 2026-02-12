package com.ab.cmsBackend.repository;

import com.ab.cmsBackend.entity.Contact;
import com.ab.cmsBackend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Long> {
    Page<Contact> findByUser(User user, Pageable pageable);

    @Query("SELECT c FROM Contact c WHERE c.user = :user AND (c.firstName LIKE %:query% OR c.lastName LIKE %:query%)")
    List<Contact> searchByUserAndQuery(User user, String query);
}