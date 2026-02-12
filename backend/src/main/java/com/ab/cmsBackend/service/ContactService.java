package com.ab.cmsBackend.service;
import com.ab.cmsBackend.dto.ContactDto;
import com.ab.cmsBackend.entity.Contact;
import com.ab.cmsBackend.entity.User;
import com.ab.cmsBackend.repository.ContactRepository;
import com.ab.cmsBackend.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContactService {

    private final ContactRepository contactRepository;
    private final UserRepository userRepository;

    public ContactService(ContactRepository contactRepository, UserRepository userRepository) {
        this.contactRepository = contactRepository;
        this.userRepository = userRepository;
    }

    private User getCurrentUser() {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public Page<ContactDto> getAllContacts(int page, int size) {
        User user = getCurrentUser();
        Page<Contact> contacts = contactRepository.findByUser(user, PageRequest.of(page, size));
        return contacts.map(this::toDto);
    }

    public List<ContactDto> searchContacts(String query) {
        User user = getCurrentUser();
        return contactRepository.searchByUserAndQuery(user, query).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public ContactDto createContact(ContactDto dto) {
        User user = getCurrentUser();
        Contact contact = toEntity(dto);
        contact.setUser(user); // Links the contact to the logged-in user
        return toDto(contactRepository.save(contact));
    }

    public ContactDto updateContact(Long id, ContactDto dto) {
        User user = getCurrentUser();
        // Securely find the contact: Ensure it belongs to the current user
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contact not found"));

        if (!contact.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You do not have permission to update this contact");
        }

        // Update all fields from the DTO
        contact.setFirstName(dto.getFirstName());
        contact.setLastName(dto.getLastName());
        contact.setTitle(dto.getTitle());
        contact.setEmailWork(dto.getEmailWork());
        contact.setEmailPersonal(dto.getEmailPersonal());
        contact.setPhoneWork(dto.getPhoneWork());
        contact.setPhoneHome(dto.getPhoneHome());
        contact.setPhonePersonal(dto.getPhonePersonal());

        return toDto(contactRepository.save(contact));
    }

    public void deleteContact(Long id) {
        User user = getCurrentUser();
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contact not found"));

        // Security check: Only the owner can delete
        if (!contact.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized delete attempt");
        }

        contactRepository.delete(contact);
    }

    public ContactDto getContactById(Long id) {
        User user = getCurrentUser();
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contact not found"));

        // Security check: Only the owner can view
        if (!contact.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        return toDto(contact);
    }

    private ContactDto toDto(Contact entity) {
        ContactDto dto = new ContactDto();
        dto.setId(entity.getId());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setTitle(entity.getTitle());
        dto.setEmailWork(entity.getEmailWork());
        dto.setEmailPersonal(entity.getEmailPersonal());
        dto.setPhoneWork(entity.getPhoneWork());
        dto.setPhoneHome(entity.getPhoneHome());
        dto.setPhonePersonal(entity.getPhonePersonal());
        return dto;
    }

    private Contact toEntity(ContactDto dto) {
        Contact entity = new Contact();
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setTitle(dto.getTitle());
        entity.setEmailWork(dto.getEmailWork());
        entity.setEmailPersonal(dto.getEmailPersonal());
        entity.setPhoneWork(dto.getPhoneWork());
        entity.setPhoneHome(dto.getPhoneHome());
        entity.setPhonePersonal(dto.getPhonePersonal());
        return entity;
    }
}

