package com.ab.cmsBackend;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Disabled("Disabling for now while focusing on repository tests")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
class ContactManangementSystemApplicationTests {

    @Test
    void contextLoads() {
        // This test checks if the Spring context loads
    }
}