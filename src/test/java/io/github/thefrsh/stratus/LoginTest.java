package io.github.thefrsh.stratus;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.thefrsh.stratus.transfer.LoginCredentialsTransfer;
import io.github.thefrsh.stratus.transfer.TokenTransfer;
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

import static org.junit.Assert.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@ActiveProfiles({"default", "test"})
@PropertySource("classpath:application-test.properties")
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LoginTest
{
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void loginAttempt_existingUser_shouldReturnOkWithToken() throws Exception
    {
        var loginCredentials = LoginCredentialsTransfer.builder()
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

        assertNotNull(objectMapper.readValue(json, TokenTransfer.class).getToken());
    }

    @Test
    public void loginAttempt_nonExistingUser_shouldReturnUnauthorized() throws Exception
    {
        var loginCredentials = LoginCredentialsTransfer.builder()
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
