package io.github.thefrsh.stratus;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.thefrsh.stratus.model.User;
import io.github.thefrsh.stratus.repository.UserJpaRepository;
import io.github.thefrsh.stratus.transfer.request.LoginCredentialsRequest;
import io.github.thefrsh.stratus.transfer.response.TokenResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@AutoConfigureMockMvc
@AutoConfigureDataJpa
@RunWith(value = SpringRunner.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LoginTest
{
    private static final String TEST_USERNAME = "test";
    private static final String TEST_PASSWORD = "test";
    private static final String TEST_EMAIL = "test@test.com";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void seedTestUser()
    {
        var user = User.builder()
                .username(TEST_USERNAME)
                .password(passwordEncoder.encode(TEST_PASSWORD))
                .email(TEST_EMAIL)
                .build();

        System.out.println(passwordEncoder.encode(TEST_PASSWORD));

        userJpaRepository.save(user);
    }

    @Test
    public void loginAttempt_existingUser_shouldReturnOkWithToken() throws Exception
    {
        seedTestUser();

        var loginCredentials = LoginCredentialsRequest.builder()
                .username("test")
                .password("test")
                .build();

        var json = mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginCredentials)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var tokenTransfer = objectMapper.readValue(json, TokenResponse.class);

        assertNotNull(tokenTransfer.getId());
        assertNotNull(tokenTransfer.getToken());
    }

    @Test
    public void loginAttempt_nonExistingUser_shouldReturnUnauthorized() throws Exception
    {
        var loginCredentials = LoginCredentialsRequest.builder()
                .username("non-existing")
                .password("test")
                .build();

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginCredentials)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void loginAttempt_incorrectBody_shouldReturnBadRequest() throws Exception
    {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{'incorrect' : 'body'}"))
                .andExpect(status().isBadRequest());
    }
}
