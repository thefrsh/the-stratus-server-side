package io.github.thefrsh.stratus;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.thefrsh.stratus.transfer.RegisterCredentialsTransfer;
import io.github.thefrsh.stratus.transfer.UserTransfer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.Assert.*;

@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@ActiveProfiles({"default", "test"})
@PropertySource("classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RegisterTest
{
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    MockMvc mockMvc;

    @Test
    public void registerAttempt_correctCredentials_shouldReturnCreatedAndUserTransfer() throws Exception
    {
        var registerCredentials = RegisterCredentialsTransfer.builder()
                .username("newuser")
                .password("Password1")
                .email("newuser@newuser.com")
                .build();

        var json = mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerCredentials)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var userTransfer = objectMapper.readValue(json, UserTransfer.class);

        assertNotNull(userTransfer.getId());
        assertEquals("newuser", userTransfer.getUsername());
    }

    @Test
    public void registerAttempt_incorrectBody_shouldReturnBadRequest() throws Exception
    {
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{'incorrect' : 'body'}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void registerAttempt_blanksInUsername_shouldReturnBadRequest() throws Exception
    {
        var registerCredentials = RegisterCredentialsTransfer.builder()
                .username("newuser   ")
                .password("Password1")
                .email("newuser@newuser.com")
                .build();

        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerCredentials)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void registerAttempt_passwordTooShort_shouldReturnBadRequest() throws Exception
    {
        var registerCredentials = RegisterCredentialsTransfer.builder()
                .username("newuser")
                .password("Pass1")
                .email("newuser@newuser.com")
                .build();

        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerCredentials)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void registerAttempt_passwordNotContainingDigit_shouldReturnBadRequest() throws Exception
    {
        var registerCredentials = RegisterCredentialsTransfer.builder()
                .username("newuser")
                .password("Password")
                .email("newuser@newuser.com")
                .build();

        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerCredentials)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void registerAttempt_passwordNotContainingUpperLetter_shouldReturnBadRequest() throws Exception
    {
        var registerCredentials = RegisterCredentialsTransfer.builder()
                .username("newuser")
                .password("password1")
                .email("newuser@newuser.com")
                .build();

        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerCredentials)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void registerAttempt_incorrectEmailAddressPattern_shouldReturnBadRequest() throws Exception
    {
        var registerCredentials = RegisterCredentialsTransfer.builder()
                .username("newuser")
                .password("Password1")
                .email("newuser.newuser.com")
                .build();

        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerCredentials)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void registerAttempt_usernameIsNotAvailable_shouldReturnConflict() throws Exception
    {
        var firstUser = RegisterCredentialsTransfer.builder()
                .username("usernameNotAvailable")
                .password("Password1")
                .email("usernameNot@available.com")
                .build();

        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(firstUser)))
                .andExpect(status().isCreated());

        var secondUser = RegisterCredentialsTransfer.builder()
                .username("usernameNotAvailable")
                .password("Password1")
                .email("availableEmail@available.com")
                .build();

        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(secondUser)))
                .andExpect(status().isConflict());
    }

    @Test
    public void registerAttempt_emailIsNotAvailable_shouldReturnConflict() throws Exception
    {
        var firstUser = RegisterCredentialsTransfer.builder()
                .username("availableUsername")
                .password("Password1")
                .email("notAvailable@email.com")
                .build();

        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(firstUser)))
                .andExpect(status().isCreated());

        var secondUser = RegisterCredentialsTransfer.builder()
                .username("availableUsername2")
                .password("Password1")
                .email("notAvailable@email.com")
                .build();

        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(secondUser)))
                .andExpect(status().isConflict());
    }
}
