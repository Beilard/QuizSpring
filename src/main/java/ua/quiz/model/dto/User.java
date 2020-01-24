package ua.quiz.model.dto;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.Collection;
import java.util.Collections;

//TODO: solve get problems
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder=true)
public class User implements UserDetails {
    private Long id;

    @NotEmpty(message = "Please, provide an email")
    @Pattern(regexp = "^(.+)@(.+)$", message = "Email does't match the pattern: example@gmail.com")
    private String email;

    @NotEmpty(message = "Please, provide a password")
    @Pattern(regexp = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})", message = "Password should be 6 to 20 symbols, with a special symbol")
    private String password;

    @NotEmpty(message = "Please, provide a name")
    @Pattern(regexp = "^[a-zA-Zа-яА-Яієї']{2,25}$", message = "Name should be at least 2 characters long and have no numbers")
    private String name;

    @NotEmpty(message = "Please, provide a surname")
    @Pattern(regexp = "^[a-zA-Zа-яА-Яієї']{2,25}$", message = "Surname should be at least 2 characters long and have no numbers")
    private String surname;

    @NotEmpty(message = "Please, provide isCaptain")
    private Boolean isCaptain;

    private Team team;

    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(getRole());
    }

    @Override
    public String getUsername() {
        return getEmail();
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
