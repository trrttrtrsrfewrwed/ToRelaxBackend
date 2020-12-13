package backend.service;

import backend.exception.ResourceNotFoundException;
import backend.model.JwtUserDetails;
import backend.model.request_bodies.UserRequestBody;
import backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException
    {
        try {
            UserRequestBody user = userService.getByEmail(email);
            return new JwtUserDetails(user);
        } catch (ResourceNotFoundException e) {
            throw new UsernameNotFoundException("User not found with username: " + email);
        }
    }
}