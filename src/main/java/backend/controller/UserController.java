package backend.controller;

import backend.config.JwtTokenUtil;
import backend.model.JwtUserDetails;
import backend.model.request_bodies.StringRequestBody;
import backend.model.request_bodies.UserRequestBody;
import backend.exception.BadRequestException;
import backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Date;


@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*")
@RestController
@RequestMapping("/api/v1/")
public class UserController {

    Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @PostMapping("/users/signup")
    public UserRequestBody signUp(@RequestBody UserRequestBody user) {
        user.setData("[]");
        final UserDetails userDetails = new JwtUserDetails(user);

        logger.info(user.getEmail());
        logger.info(user.getPassword());

        user = userService.addUser(user);

        final String token = jwtTokenUtil.generateToken(userDetails);
        user.setToken(token);

        logger.info("BACKEND: token:" + token);
        logger.info("BACKEND: user.token:" + user.getToken());

        final Date expiration = jwtTokenUtil.getExpirationDateFromToken(token);
        logger.info("EXPIRATION DATE: " + expiration);
        logger.info("CURRENT DATE: " + new Date());

        return user;
    }

    @PostMapping("/users/login/{email}")
    public UserRequestBody login(@PathVariable String email, @RequestBody StringRequestBody password) {
        UserRequestBody user = userService.getByEmail(email);
        final UserDetails userDetails = new JwtUserDetails(user);

        if (!userDetails.getPassword().equals(password.getRequestBody())) {
            throw new BadRequestException("Incorrect email or password");
        }

        final String token = jwtTokenUtil.generateToken(userDetails);
        user.setToken(token);

        return user;
    }

    @PutMapping("/users/setdata/{email}")
    public UserRequestBody setData(@PathVariable String email, @RequestBody StringRequestBody data) {
        UserRequestBody user = userService.getByEmail(email);
        return userService.changeData(user, data.getRequestBody());
    }
}