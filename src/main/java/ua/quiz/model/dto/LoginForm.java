package ua.quiz.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class LoginForm {
    @Email(message = "*Please provide a valid Email")
    @Pattern(regexp = "[a-zA-Z0-9]{1,}[@]{1}[a-z]{3,}[.]{1}+[a-z]{2,}")
    @NotEmpty(message = "Please provide an email")
    private String email;

    @NotEmpty(message = "Please provide a password")
    @Pattern(regexp = "[A-Za-zA-Яа-яёЁ!_#$%^&*()-=+-]{2,32}")
    private String password;
}
