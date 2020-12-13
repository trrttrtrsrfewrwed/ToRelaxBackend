package backend.service.impl;

import backend.model.request_bodies.UserRequestBody;
import backend.exception.BadRequestException;
import backend.exception.ResourceNotFoundException;
import backend.model.User;
import backend.repository.UserRepository;
import backend.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@RunWith(SpringRunner.class)
public class UserServiceImplTest {

    @TestConfiguration
    static class UserServiceImplTestConfig {

        @Bean
        public UserService userService() {
            return new UserServiceImpl();
        }
    }

    @Autowired
    private UserService userServiceImpl;

    @MockBean
    private UserRepository userRepository;

    @Before
    public void setup() {
        User testUser = new User(1, "test", "email", "qwerty", "[]".getBytes());

        Mockito.when(userRepository.getByEmail("email"))
                .thenReturn(testUser);

        Mockito.when(userRepository.countByEmail("email"))
                .thenReturn(1);

        Mockito.when(userRepository.getByEmail("notexist"))
                .thenReturn(null);

        Mockito.when(userRepository.countByEmail("notexist"))
                .thenReturn(0);
    }

    @Test
    public void getByEmail() {
        UserRequestBody actualUser = userServiceImpl.getByEmail("email");
        assertEquals("email", actualUser.getEmail());

        // Trying to get not existing user
        assertThrows(ResourceNotFoundException.class, () ->
                userServiceImpl.getByEmail("notexist"));
    }

    @Test
    public void changeData() {
        UserRequestBody testBody = new UserRequestBody(1, "test", "email", "qwerty", "[old data]");

        User changed = new User(1, "test", "email", "qwerty", "[new data]".getBytes());

        Mockito.when(userRepository.saveAndFlush(any(User.class)))
                .thenReturn(changed);

        UserRequestBody actualBody = userServiceImpl.changeData(testBody, "[new data]");

        assertEquals("[new data]", actualBody.getData());
    }


    @Test
    public void addUser() {
        UserRequestBody testBody = new UserRequestBody(1, "test", "another email", "qwerty", "[]");
        User testUser = new User(1, "test", "another email", "qwerty", "[]".getBytes());

        Mockito.when(userRepository.saveAndFlush(any(User.class)))
                .thenReturn(testUser);

        assertEquals(userServiceImpl.addUser(testBody).getId(), testBody.getId());

        // Trying to add user with existing email
        assertThrows(BadRequestException.class, () -> userServiceImpl.addUser(new UserRequestBody(1, "test", "email", "qwerty", "[]")));
    }
}