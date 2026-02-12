package com.ab.cmsBackend.controller;

import com.ab.cmsBackend.dto.ContactDto;
import com.ab.cmsBackend.exception.GlobalExceptionHandler;
import com.ab.cmsBackend.service.ContactService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WithMockUser(username = "test@example.com")
class ContactControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ContactService contactService;

    @InjectMocks
    private ContactController contactController;

    private ObjectMapper objectMapper;
    private ContactDto contactDto;

    @BeforeEach
    void setUp() {
        GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

        mockMvc = MockMvcBuilders.standaloneSetup(contactController)
                .setControllerAdvice(exceptionHandler)
                .build();

        objectMapper = new ObjectMapper();

        contactDto = new ContactDto();
        contactDto.setId(1L);
        contactDto.setFirstName("John");
        contactDto.setLastName("Doe");
        contactDto.setTitle("Software Engineer");
        contactDto.setEmailWork("john.doe@company.com");
    }

    // Succes Tests

    @Test
    void testGetAllContacts_Success() throws Exception {
        Page<ContactDto> page = new PageImpl<>(
                Arrays.asList(contactDto),
                PageRequest.of(0, 10),
                1
        );

        when(contactService.getAllContacts(0, 10)).thenReturn(page);

        mockMvc.perform(get("/api/contacts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].firstName").value("John"));
    }

    @Test
    void testGetAllContacts_WithCustomPagination() throws Exception {
        Page<ContactDto> page = new PageImpl<>(
                Arrays.asList(contactDto),
                PageRequest.of(2, 5),
                15
        );

        when(contactService.getAllContacts(2, 5)).thenReturn(page);

        mockMvc.perform(get("/api/contacts")
                        .param("page", "2")
                        .param("size", "5"))
                .andExpect(status().isOk());
    }

    @Test
    void testSearchContacts_Success() throws Exception {
        List<ContactDto> contacts = Arrays.asList(contactDto);
        when(contactService.searchContacts("John")).thenReturn(contacts);

        mockMvc.perform(get("/api/contacts/search")
                        .param("query", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstName").value("John"));
    }

    @Test
    void testSearchContacts_NoResults() throws Exception {
        when(contactService.searchContacts("Nonexistent")).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/contacts/search")
                        .param("query", "Nonexistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testCreateContact_Success() throws Exception {
        ContactDto newContact = new ContactDto();
        newContact.setFirstName("Jane");
        newContact.setLastName("Smith");
        newContact.setEmailWork("jane@company.com");

        ContactDto savedContact = new ContactDto();
        savedContact.setId(2L);
        savedContact.setFirstName("Jane");
        savedContact.setLastName("Smith");
        savedContact.setEmailWork("jane@company.com");

        when(contactService.createContact(any(ContactDto.class))).thenReturn(savedContact);

        mockMvc.perform(post("/api/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newContact)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.firstName").value("Jane"));
    }

    @Test
    void testUpdateContact_Success() throws Exception {
        Long contactId = 1L;
        ContactDto updateDto = new ContactDto();
        updateDto.setFirstName("John Updated");
        updateDto.setLastName("Doe Updated");

        ContactDto updatedContact = new ContactDto();
        updatedContact.setId(contactId);
        updatedContact.setFirstName("John Updated");
        updatedContact.setLastName("Doe Updated");

        when(contactService.updateContact(eq(contactId), any(ContactDto.class))).thenReturn(updatedContact);

        mockMvc.perform(put("/api/contacts/{id}", contactId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John Updated"));
    }

    @Test
    void testDeleteContact_Success() throws Exception {
        Long contactId = 1L;
        doNothing().when(contactService).deleteContact(contactId);

        mockMvc.perform(delete("/api/contacts/{id}", contactId))
                .andExpect(status().isOk());
    }

    @Test
    void testGetContactById_Success() throws Exception {
        Long contactId = 1L;
        when(contactService.getContactById(contactId)).thenReturn(contactDto);

        mockMvc.perform(get("/api/contacts/{id}", contactId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void testCreateContact_MinimalFields() throws Exception {
        String minimalJson = """
            {
                "firstName": "Minimal",
                "lastName": "Contact"
            }
            """;

        ContactDto savedContact = new ContactDto();
        savedContact.setId(3L);
        savedContact.setFirstName("Minimal");
        savedContact.setLastName("Contact");

        when(contactService.createContact(any(ContactDto.class))).thenReturn(savedContact);

        mockMvc.perform(post("/api/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(minimalJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.firstName").value("Minimal"));
    }

    // Validation Tests
    @Test
    void testUpdateContact_EmptyRequestBody() throws Exception {
        Long contactId = 1L;

        mockMvc.perform(put("/api/contacts/{id}", contactId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateContact_PartialUpdate() throws Exception {
        Long contactId = 1L;
        String partialJson = """
            {
                "firstName": "UpdatedName"
            }
            """;

        mockMvc.perform(put("/api/contacts/{id}", contactId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(partialJson))
                .andExpect(status().isBadRequest());
    }

    // Exception Tests

    @Test
    void testCreateContact_ServiceException() throws Exception {
        when(contactService.createContact(any(ContactDto.class)))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(post("/api/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contactDto)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("An error occurred: Database error"));
    }

    @Test
    void testUpdateContact_NotFound() throws Exception {
        Long contactId = 999L;
        when(contactService.updateContact(eq(contactId), any(ContactDto.class)))
                .thenThrow(new RuntimeException("Contact not found"));

        mockMvc.perform(put("/api/contacts/{id}", contactId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contactDto)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("An error occurred: Contact not found"));
    }

    @Test
    void testDeleteContact_NotFound() throws Exception {
        Long contactId = 999L;
        doThrow(new RuntimeException("Contact not found"))
                .when(contactService).deleteContact(contactId);

        mockMvc.perform(delete("/api/contacts/{id}", contactId))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("An error occurred: Contact not found"));
    }

    @Test
    void testGetContactById_NotFound() throws Exception {
        Long contactId = 999L;
        when(contactService.getContactById(contactId))
                .thenThrow(new RuntimeException("Contact not found"));

        mockMvc.perform(get("/api/contacts/{id}", contactId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("An error occurred: Contact not found"));
    }

    @Test
    void testGetContactById_InvalidIdFormat() throws Exception {
        mockMvc.perform(get("/api/contacts/abc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testUpdateContact_Unauthorized() throws Exception {
        Long contactId = 1L;
        when(contactService.updateContact(eq(contactId), any(ContactDto.class)))
                .thenThrow(new RuntimeException("You do not have permission to update this contact"));

        mockMvc.perform(put("/api/contacts/{id}", contactId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contactDto)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("An error occurred: You do not have permission to update this contact"));
    }

    @Test
    void testDeleteContact_Unauthorized() throws Exception {
        Long contactId = 1L;
        doThrow(new RuntimeException("Unauthorized delete attempt"))
                .when(contactService).deleteContact(contactId);

        mockMvc.perform(delete("/api/contacts/{id}", contactId))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("An error occurred: Unauthorized delete attempt"));
    }

    @Test
    void testGetContactById_AccessDenied() throws Exception {
        Long contactId = 1L;
        when(contactService.getContactById(contactId))
                .thenThrow(new RuntimeException("Access denied"));

        mockMvc.perform(get("/api/contacts/{id}", contactId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("An error occurred: Access denied"));
    }
}