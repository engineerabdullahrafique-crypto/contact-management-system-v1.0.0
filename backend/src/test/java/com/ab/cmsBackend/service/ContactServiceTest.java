package com.ab.cmsBackend.service;

import com.ab.cmsBackend.dto.ContactDto;
import com.ab.cmsBackend.entity.Contact;
import com.ab.cmsBackend.entity.User;
import com.ab.cmsBackend.repository.ContactRepository;
import com.ab.cmsBackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

// This annotation tells JUnit to use Mockito for mocking
@ExtendWith(MockitoExtension.class)
class ContactServiceTest {

    // Mock objects (fake objects) for dependencies
    @Mock
    private ContactRepository contactRepository;  // Fake contact database

    @Mock
    private UserRepository userRepository;  // Fake user database

    @Mock
    private SecurityContext securityContext;  // Fake security context

    @Mock
    private Authentication authentication;  // Fake authentication

    // The service we want to test
    @InjectMocks
    private ContactService contactService;

    // Test data
    private User currentUser;
    private User otherUser;
    private Contact contact1;
    private Contact contact2;
    private ContactDto contactDto;

    // This runs before each test
    @BeforeEach
    void setUp() {
        // Setup current user (logged in user)
        currentUser = new User();
        currentUser.setId(1L);
        currentUser.setEmail("current@example.com");
        currentUser.setPhone("1234567890");
        currentUser.setPassword("password");

        // Setup another user (not logged in)
        otherUser = new User();
        otherUser.setId(2L);
        otherUser.setEmail("other@example.com");
        otherUser.setPhone("0987654321");
        otherUser.setPassword("password456");

        // Setup first contact (belongs to current user)
        contact1 = new Contact();
        contact1.setId(1L);
        contact1.setFirstName("John");
        contact1.setLastName("Doe");
        contact1.setTitle("Manager");
        contact1.setEmailWork("john@work.com");
        contact1.setEmailPersonal("john@personal.com");
        contact1.setPhoneWork("111-222-3333");
        contact1.setPhoneHome("444-555-6666");
        contact1.setPhonePersonal("777-888-9999");
        contact1.setUser(currentUser);

        // Setup second contact (also belongs to current user)
        contact2 = new Contact();
        contact2.setId(2L);
        contact2.setFirstName("Jane");
        contact2.setLastName("Smith");
        contact2.setTitle("Developer");
        contact2.setEmailWork("jane@work.com");
        contact2.setEmailPersonal("jane@personal.com");
        contact2.setPhoneWork("999-888-7777");
        contact2.setPhoneHome("666-555-4444");
        contact2.setPhonePersonal("333-222-1111");
        contact2.setUser(currentUser);

        // Setup contact DTO for creating/updating
        contactDto = new ContactDto();
        contactDto.setFirstName("New");
        contactDto.setLastName("Contact");
        contactDto.setTitle("Designer");
        contactDto.setEmailWork("new@work.com");
        contactDto.setEmailPersonal("new@personal.com");
        contactDto.setPhoneWork("111-111-1111");
        contactDto.setPhoneHome("222-222-2222");
        contactDto.setPhonePersonal("333-333-3333");
    }

    // Helper method to mock the security context (logged in user)
    private void mockSecurityContext() {
        // Set up the security context to return current user's email
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn("current@example.com");
        SecurityContextHolder.setContext(securityContext);

        // When userRepository looks for this email, return currentUser
        when(userRepository.findByEmail("current@example.com")).thenReturn(Optional.of(currentUser));
    }

    // Get All Contacts Test

    @Test
    void getAllContacts_ShouldReturnContacts_WhenUserIsLoggedIn() {
        // Given: Mock security context and repository
        mockSecurityContext();

        // Create a page of contacts
        Page<Contact> contactPage = new PageImpl<>(Arrays.asList(contact1, contact2));
        when(contactRepository.findByUser(eq(currentUser), any(PageRequest.class)))
                .thenReturn(contactPage);

        // When: Call getAllContacts
        Page<ContactDto> result = contactService.getAllContacts(0, 10);

        // Then: Should return 2 contacts
        assertEquals(2, result.getContent().size());
        assertEquals("John", result.getContent().get(0).getFirstName());
        assertEquals("Jane", result.getContent().get(1).getFirstName());

        // Verify mocks were called
        verify(userRepository).findByEmail("current@example.com");
        verify(contactRepository).findByUser(eq(currentUser), any(PageRequest.class));
    }

    @Test
    void getAllContacts_ShouldReturnEmpty_WhenUserHasNoContacts() {
        // Given: Mock security context
        mockSecurityContext();

        // Empty page for contacts
        Page<Contact> emptyPage = new PageImpl<>(List.of());
        when(contactRepository.findByUser(eq(currentUser), any(PageRequest.class)))
                .thenReturn(emptyPage);

        // When: Call getAllContacts
        Page<ContactDto> result = contactService.getAllContacts(0, 10);

        // Then: Should be empty
        assertTrue(result.getContent().isEmpty());
    }

    @Test
    void getAllContacts_ShouldThrowException_WhenUserNotFound() {
        // Given: Mock security context with non-existent user
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn("nonexistent@example.com");
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // When & Then: Should throw exception
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            contactService.getAllContacts(0, 10);
        });

        assertEquals("User not found", exception.getMessage());
    }

    // Search Contact Test

    @Test
    void searchContacts_ShouldReturnMatchingContacts() {
        // Given: Mock security context
        mockSecurityContext();

        // Mock repository to return contacts containing "John"
        when(contactRepository.searchByUserAndQuery(currentUser, "John"))
                .thenReturn(Arrays.asList(contact1));

        // When: Search for "John"
        List<ContactDto> result = contactService.searchContacts("John");

        // Then: Should find 1 contact
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());

        verify(contactRepository).searchByUserAndQuery(currentUser, "John");
    }

    @Test
    void searchContacts_ShouldReturnEmpty_WhenNoMatches() {
        // Given: Mock security context
        mockSecurityContext();

        // Mock empty results
        when(contactRepository.searchByUserAndQuery(currentUser, "NoMatch"))
                .thenReturn(List.of());

        // When: Search for non-existent name
        List<ContactDto> result = contactService.searchContacts("NoMatch");

        // Then: Should be empty
        assertTrue(result.isEmpty());
    }

    @Test
    void searchContacts_ShouldReturnAll_WhenEmptyQuery() {
        // Given: Mock security context
        mockSecurityContext();

        // Mock returning all contacts for empty query
        when(contactRepository.searchByUserAndQuery(currentUser, ""))
                .thenReturn(Arrays.asList(contact1, contact2));

        // When: Search with empty string
        List<ContactDto> result = contactService.searchContacts("");

        // Then: Should return all 2 contacts
        assertEquals(2, result.size());
    }

    // Create Contact Test

    @Test
    void createContact_ShouldSaveNewContact() {
        // Given: Mock security context
        mockSecurityContext();

        // Mock save to return contact with ID
        Contact newContact = new Contact();
        newContact.setFirstName("New");
        newContact.setLastName("Contact");
        newContact.setUser(currentUser);
        newContact.setId(3L);

        when(contactRepository.save(any(Contact.class))).thenReturn(newContact);

        // When: Create new contact
        ContactDto result = contactService.createContact(contactDto);

        // Then: Should return saved contact DTO
        assertNotNull(result);
        assertEquals("New", result.getFirstName());
        assertEquals("Contact", result.getLastName());

        // Verify save was called with correct user
        verify(contactRepository).save(argThat(contact ->
                contact.getUser() == currentUser
        ));
    }

    // Update COntact Test

    @Test
    void updateContact_ShouldUpdate_WhenUserOwnsContact() {
        // Given: Mock security context
        mockSecurityContext();

        // Mock finding contact
        when(contactRepository.findById(1L)).thenReturn(Optional.of(contact1));
        when(contactRepository.save(any(Contact.class))).thenReturn(contact1);

        // When: Update contact
        ContactDto result = contactService.updateContact(1L, contactDto);

        // Then: Should return updated contact
        assertNotNull(result);

        // Verify contact was found and saved
        verify(contactRepository).findById(1L);
        verify(contactRepository).save(contact1);
    }

    @Test
    void updateContact_ShouldThrowException_WhenContactNotFound() {
        // Given: Mock security context
        mockSecurityContext();

        // Mock not finding contact
        when(contactRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then: Should throw exception
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            contactService.updateContact(999L, contactDto);
        });

        assertEquals("Contact not found", exception.getMessage());
    }

    @Test
    void updateContact_ShouldThrowException_WhenUserDoesNotOwnContact() {
        // Given: Mock security context
        mockSecurityContext();

        // Contact belongs to other user
        contact1.setUser(otherUser);
        when(contactRepository.findById(1L)).thenReturn(Optional.of(contact1));

        // When & Then: Should throw exception
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            contactService.updateContact(1L, contactDto);
        });

        assertEquals("You do not have permission to update this contact", exception.getMessage());
    }

    // Delete Contact Test

    @Test
    void deleteContact_ShouldDelete_WhenUserOwnsContact() {
        // Given: Mock security context
        mockSecurityContext();

        // Mock finding contact
        when(contactRepository.findById(1L)).thenReturn(Optional.of(contact1));
        doNothing().when(contactRepository).delete(contact1);

        // When: Delete contact
        contactService.deleteContact(1L);

        // Then: Should delete without exception
        verify(contactRepository).delete(contact1);
    }

    @Test
    void deleteContact_ShouldThrowException_WhenUserDoesNotOwnContact() {
        // Given: Mock security context
        mockSecurityContext();

        // Contact belongs to other user
        contact1.setUser(otherUser);
        when(contactRepository.findById(1L)).thenReturn(Optional.of(contact1));

        // When & Then: Should throw exception
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            contactService.deleteContact(1L);
        });

        assertEquals("Unauthorized delete attempt", exception.getMessage());

        // Should NOT delete
        verify(contactRepository, never()).delete(any());
    }

    // Get Contact By ID Test

    @Test
    void getContactById_ShouldReturnContact_WhenUserOwnsContact() {
        // Given: Mock security context
        mockSecurityContext();

        // Mock finding contact
        when(contactRepository.findById(1L)).thenReturn(Optional.of(contact1));

        // When: Get contact by ID
        ContactDto result = contactService.getContactById(1L);

        // Then: Should return contact
        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
    }

    @Test
    void getContactById_ShouldThrowException_WhenUserDoesNotOwnContact() {
        // Given: Mock security context
        mockSecurityContext();

        // Contact belongs to other user
        contact1.setUser(otherUser);
        when(contactRepository.findById(1L)).thenReturn(Optional.of(contact1));

        // When & Then: Should throw exception
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            contactService.getContactById(1L);
        });

        assertEquals("Access denied", exception.getMessage());
    }

    @Test
    void getContactById_ShouldThrowException_WhenContactNotFound() {
        // Given: Mock security context
        mockSecurityContext();

        // Mock not finding contact
        when(contactRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then: Should throw exception
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            contactService.getContactById(999L);
        });

        assertEquals("Contact not found", exception.getMessage());
    }

    // DTO/Entity Conversion Test

    @Test
    void toDto_ShouldConvertContactToDto() {
        // This is a private method, but we can test it indirectly through public methods

        // Given: Mock security context
        mockSecurityContext();

        // Mock repository
        when(contactRepository.findById(1L)).thenReturn(Optional.of(contact1));

        // When: Get contact (which calls toDto internally)
        ContactDto result = contactService.getContactById(1L);

        // Then: All fields should be converted correctly
        assertEquals(1L, result.getId());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("Manager", result.getTitle());
        assertEquals("john@work.com", result.getEmailWork());
        assertEquals("john@personal.com", result.getEmailPersonal());
        assertEquals("111-222-3333", result.getPhoneWork());
        assertEquals("444-555-6666", result.getPhoneHome());
        assertEquals("777-888-9999", result.getPhonePersonal());
    }

    @Test
    void toEntity_ShouldConvertDtoToContact() {
        // This is a private method, but we can test it indirectly through createContact

        // Given: Mock security context
        mockSecurityContext();

        // Create a DTO with all fields
        ContactDto testDto = new ContactDto();
        testDto.setFirstName("Test");
        testDto.setLastName("User");
        testDto.setTitle("Tester");
        testDto.setEmailWork("test@work.com");
        testDto.setEmailPersonal("test@personal.com");
        testDto.setPhoneWork("111");
        testDto.setPhoneHome("222");
        testDto.setPhonePersonal("333");

        // Mock save
        Contact savedContact = new Contact();
        savedContact.setId(1L);
        savedContact.setFirstName("Test");
        savedContact.setLastName("User");
        savedContact.setUser(currentUser);

        when(contactRepository.save(any(Contact.class))).thenReturn(savedContact);

        // When: Create contact (which calls toEntity internally)
        ContactDto result = contactService.createContact(testDto);

        // Then: Should save successfully
        assertNotNull(result);
        assertEquals("Test", result.getFirstName());

        // Verify save was called with correct data
        verify(contactRepository).save(argThat(contact ->
                contact.getFirstName().equals("Test") &&
                        contact.getLastName().equals("User")
        ));
    }

    // Edge Cases

    @Test
    void getAllContacts_WithDifferentPageSize() {
        // Given: Mock security context
        mockSecurityContext();

        // Create page with 1 contact (testing different page size)
        Page<Contact> contactPage = new PageImpl<>(List.of(contact1));
        when(contactRepository.findByUser(eq(currentUser), any(PageRequest.class)))
                .thenReturn(contactPage);

        // When: Get contacts with page size 1
        Page<ContactDto> result = contactService.getAllContacts(0, 1);

        // Then: Should return 1 contact
        assertEquals(1, result.getContent().size());
    }

    @Test
    void createContact_ShouldSetCurrentUser() {
        // Given: Mock security context
        mockSecurityContext();

        // Capture the saved contact to check it has the right user
        Contact savedContact = new Contact();
        savedContact.setId(3L);
        savedContact.setUser(currentUser);

        when(contactRepository.save(any(Contact.class))).thenReturn(savedContact);

        // When: Create contact
        contactService.createContact(contactDto);

        // Then: Verify contact was saved with current user
        verify(contactRepository).save(argThat(contact ->
                contact.getUser() == currentUser
        ));
    }

    @Test
    void searchContacts_WithNullQuery() {
        // Given: Mock security context
        mockSecurityContext();

        // Mock returning no results for null
        when(contactRepository.searchByUserAndQuery(currentUser, null))
                .thenReturn(List.of());

        // When: Search with null
        List<ContactDto> result = contactService.searchContacts(null);

        // Then: Should return empty list
        assertTrue(result.isEmpty());
    }
}