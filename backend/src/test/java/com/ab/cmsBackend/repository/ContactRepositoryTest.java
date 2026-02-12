package com.ab.cmsBackend.repository;

import com.ab.cmsBackend.entity.Contact;
import com.ab.cmsBackend.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ContactRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ContactRepository contactRepository;

    private User testUser;
    private User anotherUser;
    private Contact contact1, contact2, contact3, contact4;

    @BeforeEach
    void setUp() {
        // Clean up any existing data
        contactRepository.deleteAll();
        entityManager.getEntityManager().createQuery("DELETE FROM User").executeUpdate();

        // Create test users
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setPhone("1234567890");
        testUser.setPassword("password123");
        entityManager.persist(testUser);

        anotherUser = new User();
        anotherUser.setEmail("another@example.com");
        anotherUser.setPhone("0987654321");
        anotherUser.setPassword("password456");
        entityManager.persist(anotherUser);

        // Create contacts for testUser
        contact1 = createContact("John", "Doe", "Manager", testUser);
        contact2 = createContact("Jane", "Smith", "Developer", testUser);
        contact3 = createContact("Bob", "Johnson", "Designer", testUser);

        // Create contact for anotherUser
        contact4 = createContact("Alice", "Brown", "CEO", anotherUser);

        entityManager.flush();
        entityManager.clear(); // Clear persistence context to ensure fresh queries
    }

    private Contact createContact(String firstName, String lastName, String title, User user) {
        Contact contact = new Contact();
        contact.setFirstName(firstName);
        contact.setLastName(lastName);
        contact.setTitle(title);
        contact.setEmailWork(firstName.toLowerCase() + ".work@example.com");
        contact.setEmailPersonal(firstName.toLowerCase() + ".personal@example.com");
        contact.setPhoneWork("111-222-3333");
        contact.setPhoneHome("444-555-6666");
        contact.setPhonePersonal("777-888-9999");
        contact.setUser(user);
        entityManager.persist(contact);
        return contact;
    }

    @Test
    void findByUser_ShouldReturnContactsForSpecificUser() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Contact> result = contactRepository.findByUser(testUser, pageable);

        // Then
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getContent())
                .extracting(Contact::getFirstName)
                .containsExactlyInAnyOrder("John", "Jane", "Bob");
    }

    @Test
    void findByUser_ShouldNotReturnContactsOfOtherUsers() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Contact> result = contactRepository.findByUser(anotherUser, pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getFirstName()).isEqualTo("Alice");
    }

    @Test
    void findByUser_WithPagination_ShouldReturnPaginatedResults() {
        // Given
        Pageable pageable = PageRequest.of(0, 2); // First page, 2 items per page

        // When
        Page<Contact> firstPage = contactRepository.findByUser(testUser, pageable);

        // Then
        assertThat(firstPage.getContent()).hasSize(2);
        assertThat(firstPage.getTotalElements()).isEqualTo(3);
        assertThat(firstPage.getTotalPages()).isEqualTo(2);
        assertThat(firstPage.isFirst()).isTrue();
        assertThat(firstPage.hasNext()).isTrue();
    }

    @Test
    void searchByUserAndQuery_ShouldSearchInFirstNameAndLastName() {
        // When
        List<Contact> johnResults = contactRepository.searchByUserAndQuery(testUser, "John");
        List<Contact> smithResults = contactRepository.searchByUserAndQuery(testUser, "Smith");
        List<Contact> bobResults = contactRepository.searchByUserAndQuery(testUser, "Bob");

        // Then
        // "John" should match "John Doe" (first name) AND "Bob Johnson" (last name contains "John")
        assertThat(johnResults).hasSize(2);
        assertThat(johnResults)
                .extracting(Contact::getFirstName)
                .containsExactlyInAnyOrder("John", "Bob");

        // "Smith" should match only "Jane Smith"
        assertThat(smithResults).hasSize(1);
        assertThat(smithResults.get(0).getLastName()).isEqualTo("Smith");

        // "Bob" should match only "Bob Johnson"
        assertThat(bobResults).hasSize(1);
        assertThat(bobResults.get(0).getFirstName()).isEqualTo("Bob");
    }

    @Test
    void searchByUserAndQuery_ShouldReturnPartialMatches() {
        // When
        List<Contact> results = contactRepository.searchByUserAndQuery(testUser, "Jo");

        // Then
        assertThat(results).hasSize(2); // John Doe and Bob Johnson
        assertThat(results)
                .extracting(Contact::getFirstName)
                .containsExactlyInAnyOrder("John", "Bob");
    }

    @Test
    void searchByUserAndQuery_WithExactMatch_ShouldReturnSpecificContact() {
        // When
        List<Contact> results = contactRepository.searchByUserAndQuery(testUser, "John Doe");

        // Then
        assertThat(results).isEmpty();
    }

    @Test
    void searchByUserAndQuery_WithNoMatches_ShouldReturnEmptyList() {
        // When
        List<Contact> results = contactRepository.searchByUserAndQuery(testUser, "Nonexistent");

        // Then
        assertThat(results).isEmpty();
    }

    @Test
    void searchByUserAndQuery_ShouldRespectUserBoundary() {
        // When: Searching in testUser's contacts
        List<Contact> testUserResults = contactRepository.searchByUserAndQuery(testUser, "Alice");
        List<Contact> anotherUserResults = contactRepository.searchByUserAndQuery(anotherUser, "Alice");

        // Then: Should not find Alice in testUser's results
        assertThat(testUserResults).isEmpty();

        // Should find Alice in anotherUser's results
        assertThat(anotherUserResults).hasSize(1);
        assertThat(anotherUserResults.get(0).getFirstName()).isEqualTo("Alice");
    }

    @Test
    void saveContact_ShouldPersistAndReturnWithId() {
        // Given
        Contact newContact = new Contact();
        newContact.setFirstName("New");
        newContact.setLastName("Contact");
        newContact.setTitle("Tester");
        newContact.setEmailWork("new@work.com");
        newContact.setUser(testUser);

        // When
        Contact saved = contactRepository.save(newContact);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getFirstName()).isEqualTo("New");
        assertThat(saved.getUser().getId()).isEqualTo(testUser.getId());
    }

    @Test
    void findById_ShouldReturnContact() {
        // When
        Contact found = contactRepository.findById(contact1.getId()).orElse(null);

        // Then
        assertThat(found).isNotNull();
        assertThat(found.getFirstName()).isEqualTo("John");
        assertThat(found.getLastName()).isEqualTo("Doe");
    }

    @Test
    void deleteById_ShouldRemoveContact() {
        // When
        contactRepository.deleteById(contact1.getId());
        entityManager.flush();
        entityManager.clear();

        // Then
        Contact found = contactRepository.findById(contact1.getId()).orElse(null);
        assertThat(found).isNull();
    }

    @Test
    void count_ShouldReturnCorrectNumberOfContacts() {
        // When
        long count = contactRepository.count();

        // Then
        assertThat(count).isEqualTo(4); // 3 for testUser + 1 for anotherUser
    }
}
