package backend.service.impl;

import backend.model.request_bodies.UserRequestBody;
import backend.exception.BadRequestException;
import backend.exception.ResourceNotFoundException;
import backend.model.User;
import backend.repository.UserRepository;
import backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    private User createUserFromBody(UserRequestBody body) {
        return new User(body.getId(),
                body.getName(),
                body.getEmail(),
                body.getPassword(),
                body.getData().getBytes());
    }

    private UserRequestBody createBodyFromUser(User user) {
        return new UserRequestBody(user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                new String(user.getData()));
    }

    @Override
    public UserRequestBody getByEmail(String email) {
        if (userRepository.countByEmail(email) == 0) {
            throw new ResourceNotFoundException("User doesn't exist with email: " + email);
        }
        return createBodyFromUser(userRepository.getByEmail(email));
    }

    @Override
    public UserRequestBody changeData(UserRequestBody userRequestBody, String data) {
        userRequestBody.setData(data);
        User user;
        try {
            user = userRepository.saveAndFlush(createUserFromBody(userRequestBody));
        } catch (Exception e) {
            throw new ResourceNotFoundException("Unable to change data");
        }
        return createBodyFromUser(user);
    }

    @Override
    public UserRequestBody addUser(UserRequestBody userRequestBody) {
        if (userRepository.countByEmail(userRequestBody.getEmail()) > 0) {
            throw new BadRequestException("User with such email already exists");
        }
        return createBodyFromUser((userRepository.saveAndFlush(createUserFromBody(userRequestBody))));
    }
}