package backend.controller;


import backend.config.JwtAuthenticationEntryPoint;
import backend.config.JwtRequestFilter;
import backend.config.JwtTokenUtil;
import backend.exception.BadRequestException;
import backend.exception.ResourceNotFoundException;
import backend.model.request_bodies.StringRequestBody;
import backend.model.request_bodies.UserRequestBody;
import backend.service.JwtUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import backend.service.UserService;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @TestConfiguration
    public class ApplicationSecurity extends WebSecurityConfigurerAdapter {
        @Override
        public void configure(WebSecurity web) throws Exception {
            web.ignoring().antMatchers("/**");
        }
    }


    @Autowired
    private MockMvc mvc;

    @MockBean
    private JwtUserDetailsService jwtService;

    @MockBean
    private UserService service;

    @MockBean
    private JwtTokenUtil tokenUtil;

    @MockBean
    private JwtRequestFilter requestFilter;

    @MockBean
    private JwtAuthenticationEntryPoint entryPoint;

    public static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    public void signUp()
            throws Exception {

        UserRequestBody testBody = new UserRequestBody(1, "test", "another email", "qwerty", "[]");

        when(service.addUser(any())).thenReturn(testBody);

        when(tokenUtil.generateToken(any())).thenReturn("secret");

        MvcResult result = mvc.perform(post("/api/v1/users/signup")
                .content(asJsonString(testBody))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        assertEquals(result.getResponse().getContentAsString(), "{\"id\"" + ":1,\"name\"" + ":\"test\",\"email\"" + ":\"another email\",\"password\":\"qwerty\",\"data\":\"[]\",\"token\":\"secret\"}");

        when(service.addUser(any())).thenThrow(new BadRequestException("User with such email already exists"));

        result = mvc.perform(post("/api/v1/users/signup")
                .content(asJsonString(testBody))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()).andReturn();

        assertEquals(result.getResponse().getContentAsString(), "User with such email already exists");
    }

    @Test
    public void login()
            throws Exception {
        UserRequestBody testBody = new UserRequestBody(1, "test", "email@email", "qwerty", "[]");
        UserRequestBody incorrectPasswordBody = new UserRequestBody(1, "test", "email@email", "incorekt", "[]");

        StringRequestBody body = new StringRequestBody();
        body.setRequestBody("qwerty");

        when(service.getByEmail("email@email")).thenReturn(testBody);
        when(service.getByEmail("nonexist")).thenThrow(new ResourceNotFoundException("User doesn't exist with email: " + "nonexist"));

        when(tokenUtil.generateToken(any())).thenReturn("secret");

        MvcResult result = mvc.perform(post("/api/v1/users/login/{email}", "email@email")
                .content(asJsonString(body))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        assertEquals(result.getResponse().getContentAsString(), "{\"id\"" + ":1,\"name\"" + ":\"test\",\"email\"" + ":\"email@email\",\"password\":\"qwerty\",\"data\":\"[]\",\"token\":\"secret\"}");

        when(service.getByEmail("email@email")).thenReturn(incorrectPasswordBody);

        result = mvc.perform(post("/api/v1/users/login/{email}", "email@email")
                .content(asJsonString(body))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()).andReturn();

        assertEquals(result.getResponse().getContentAsString(), "Incorrect email or password");

        result = mvc.perform(post("/api/v1/users/login/{email}", "nonexist")
                .content(asJsonString(body))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()).andReturn();

        assertEquals(result.getResponse().getContentAsString(), "User doesn't exist with email: nonexist");
    }

    @Test
    public void setdata()
            throws Exception {
        UserRequestBody testBody = new UserRequestBody(1, "test", "email@email", "qwerty", "[]");
        StringRequestBody data = new StringRequestBody();
        data.setRequestBody("[]");

        when(service.changeData(any(), eq(data.getRequestBody()))).thenReturn(testBody);


        MvcResult result = mvc.perform(put("/api/v1/users/setdata/{email}", "email@email")
                .content(asJsonString(data))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        assertEquals(result.getResponse().getContentAsString(), "{\"id\"" + ":1,\"name\"" + ":\"test\",\"email\"" + ":\"email@email\",\"password\":\"qwerty\",\"data\":\"[]\",\"token\":null}");
    }
}
