package backend.controller;

import backend.Application;
import backend.config.JwtAuthenticationEntryPoint;
import backend.config.JwtRequestFilter;
import backend.config.JwtTokenUtil;
import backend.exception.BadRequestException;
import backend.exception.ResourceNotFoundException;
import backend.model.request_bodies.StringRequestBody;
import backend.model.request_bodies.UserRequestBody;
import backend.repository.UserRepository;
import backend.service.JwtUserDetailsService;
import backend.service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;

import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = Application.class)
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    public static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void signUpAndSetData()
            throws Exception {

        UserRequestBody testBody = new UserRequestBody(1, "test", "email@email", "qwerty", "[]");

        // Sign Up
        MvcResult result = mvc.perform(post("/api/v1/users/signup")
                .content(asJsonString(testBody))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());

        assertEquals(jsonObject.get("name"), "test");
        assertEquals(jsonObject.get("password"), "qwerty");
        assertEquals(jsonObject.get("data"), "[]");
        String token = (String) jsonObject.get("token");
        assertFalse(jwtTokenUtil.isTokenExpired(token));

        // Set data
        StringRequestBody data = new StringRequestBody();
        data.setRequestBody("[lol]");

        result = mvc.perform(put("/api/v1/users/setdata/{email}", "email@email")
                .content(asJsonString(data))
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        JSONObject jsonObject2 = new JSONObject(result.getResponse().getContentAsString());

        assertEquals(jsonObject2.get("name"), "test");
        assertEquals(jsonObject2.get("password"), "qwerty");
        assertEquals(jsonObject2.get("data"), "[lol]");

        TimeUnit.MINUTES.sleep(1);


        // Token expires in one minute
        mvc.perform(put("/api/v1/users/setdata/{email}", "email@email")
        .content(asJsonString(data))
        .header("Authorization", "Bearer " + token)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());
    }
}
