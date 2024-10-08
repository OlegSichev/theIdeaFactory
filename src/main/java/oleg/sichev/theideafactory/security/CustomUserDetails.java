package oleg.sichev.theideafactory.security;

import oleg.sichev.theideafactory.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

public class CustomUserDetails implements UserDetails {
    private Long id;
    private String username;
    private String password;
    private Set<GrantedAuthority> authorities;

    public CustomUserDetails(User user, Set<GrantedAuthority> authorities) {
        this.id = Long.valueOf(user.getId());
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.authorities = authorities;
    }

    public Long getId() {
        return id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}