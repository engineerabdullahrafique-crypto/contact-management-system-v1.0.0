package com.ab.cmsBackend.controller;

import com.ab.cmsBackend.dto.ContactDto;
import com.ab.cmsBackend.service.ContactService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contacts")
public class ContactController {

    private static final Logger logger = LoggerFactory.getLogger(ContactController.class);

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping
    public ResponseEntity<Page<ContactDto>> getAllContacts(@RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size) {
        logger.info("Fetching contacts page: {}", page);
        return ResponseEntity.ok(contactService.getAllContacts(page, size));
    }


    @GetMapping("/search")
    public ResponseEntity<List<ContactDto>> searchContacts(@RequestParam String query) {
        logger.info("Searching contacts with query: {}", query);
        return ResponseEntity.ok(contactService.searchContacts(query));
    }

    @PostMapping
    public ResponseEntity<ContactDto> createContact(@RequestBody ContactDto contactDto) {
        logger.info("Creating contact: {}", contactDto.getFirstName());
        return ResponseEntity.ok(contactService.createContact(contactDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContactDto> updateContact(@PathVariable Long id, @RequestBody ContactDto contactDto) {
        logger.info("Updating contact id: {}", id);
        return ResponseEntity.ok(contactService.updateContact(id, contactDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable Long id) {
        logger.info("Deleting contact id: {}", id);
        contactService.deleteContact(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactDto> getContactById(@PathVariable Long id) {
        logger.info("Fetching contact id: {}", id);
        return ResponseEntity.ok(contactService.getContactById(id));
    }
}