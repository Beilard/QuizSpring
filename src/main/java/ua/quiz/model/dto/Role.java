package ua.quiz.model.dto;

import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;

public enum Role implements GrantedAuthority {
    PLAYER("player"), JUDGE("judge");

    String description;

    Role(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static Role valueOfName(String name) {
        return Arrays.stream(values())
                .filter(x -> x.name().equalsIgnoreCase(name))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Value of name role is null or there are no match by this name"));
    }

    @Override
    public String getAuthority() {
        return name();
    }
}
