package com.pulseinternship.bookstore;

import com.pulseinternship.bookstore.model.entities.User;
import com.pulseinternship.bookstore.model.enums.UserRole;
import com.pulseinternship.bookstore.repository.BookRepo;
import com.pulseinternship.bookstore.repository.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BookstoreApiIntegrationTests {
    private static final Pattern TOKEN_PATTERN = Pattern.compile("\\\"token\\\":\\\"([^\\\"]+)\\\"");
    private static final String BOOK_JSON = """
            {
              "title": "Clean Code",
              "author": "Robert C. Martin",
              "category": "Software Engineering",
              "price": 950.00,
              "description": "A guide to writing maintainable software.",
              "imageUrl": "https://example.com/clean-code.jpg"
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private BookRepo bookRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void cleanDatabase() {
        bookRepo.deleteAll();
        userRepo.deleteAll();
    }

    @Test
    void registrationCreatesUserWithHashedPassword() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "new-user@example.com",
                                  "password": "Password123",
                                  "confirmPassword": "Password123",
                                  "phone": "+201001234567"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("USER"));

        User user = userRepo.findByEmail("new-user@example.com").orElseThrow();
        assertThat(user.getRole()).isEqualTo(UserRole.USER);
        assertThat(user.getPassword()).isNotEqualTo("Password123");
        assertThat(passwordEncoder.matches("Password123", user.getPassword())).isTrue();
    }

    @Test
    void duplicateRegistrationReturnsConflict() throws Exception {
        createUser("existing@example.com", UserRole.USER);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "existing@example.com",
                                  "password": "Password123",
                                  "confirmPassword": "Password123",
                                  "phone": "+201001234567"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void userCanReadBooksButCannotCreateThem() throws Exception {
        createUser("user@example.com", UserRole.USER);
        String token = login("user@example.com");

        mockMvc.perform(get("/api/books").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/books")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BOOK_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void adminCanCreateBookAndValidationRejectsZeroPrice() throws Exception {
        createUser("admin@example.com", UserRole.ADMIN);
        String token = login("admin@example.com");

        mockMvc.perform(post("/api/books")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BOOK_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Clean Code"));

        mockMvc.perform(post("/api/books")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BOOK_JSON.replace("950.00", "0")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void deletedAdminTokenIsRejectedImmediately() throws Exception {
        User admin = createUser("admin@example.com", UserRole.ADMIN);
        String token = login("admin@example.com");
        userRepo.deleteById(admin.getId());

        mockMvc.perform(get("/api/admins").header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void missingTokenReturnsStructuredUnauthorizedResponse() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    private User createUser(String email, UserRole role) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("Password123"));
        user.setPhone("+201001234567");
        user.setRole(role);
        return userRepo.saveAndFlush(user);
    }

    private String login(String email) throws Exception {
        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"Password123"}
                                """.formatted(email)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Matcher matcher = TOKEN_PATTERN.matcher(response);
        assertThat(matcher.find()).isTrue();
        return matcher.group(1);
    }
}
