package backend.model;

import backend.model.request_bodies.UserRequestBody;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@SuppressWarnings("serial")
public class JwtUserDetails implements org.springframework.security.core.userdetails.UserDetails {

    public JwtUserDetails() {}

    public JwtUserDetails(UserRequestBody user) {
        this.user = user;
    }

    private UserRequestBody user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}