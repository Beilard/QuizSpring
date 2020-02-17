package ua.quiz.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.ModelAndView;
import ua.quiz.configuration.LoginSuccessHandler;
import ua.quiz.model.dto.Role;
import ua.quiz.model.dto.User;
import ua.quiz.model.service.UserService;

import java.util.Map;
import java.util.Objects;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@WebMvcTest(AuthenticationController.class)
public class AuthenticationControllerTest {
    private static final long ID = 1L;

    private static final String EMAIL = "email@mail.com";

    private static final String PASSWORD = "Password123#";

    private static final String NAME = "Name";

    private static final String SURNAME = "Surname";

    private static final User USER = User.builder()
            .id(ID)
            .email(EMAIL)
            .password(PASSWORD)
            .name(NAME)
            .surname(SURNAME)
            .role(Role.PLAYER)
            .build();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private LoginSuccessHandler loginSuccessHandler;

    @Test
    public void mainShouldReturnHomePage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(view().name("index"));
    }

    @Test
    public void loginShouldReturnLoginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(view().name("login"));
    }

    @Test
    public void registerShouldReturnRegisterPage() throws Exception {
        final ModelAndView modelAndView = mockMvc.perform(get("/register"))
                .andExpect(view().name("register"))
                .andReturn().getModelAndView();

        final Map<String, Object> model = Objects.requireNonNull(modelAndView).getModel();

        assertThat(model.get("user"), is(notNullValue()));
    }

    @Test
    public void registerShouldRegisterUser() throws Exception {
        User user = USER;

        mockMvc.perform(post("/register")
                .flashAttr("user", user)
                .param("confirmPassword", user.getPassword()))
                .andExpect(view().name("redirect:/"));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        verify(userService).register(userCaptor.capture());

        assertThat(userCaptor.getValue(), is(user));
    }

    @Test
    public void registerShouldNotRegisterUser() throws Exception {
        User user = User.builder()
                .name("Name")
                .build();

        mockMvc.perform(post("/register")
                .flashAttr("user", user)
                .param("confirmPassword", USER.getPassword()))
                .andExpect(view().name("register"));
    }

    @Test
    public void registerShouldNotRegisterUserWithInvalidPasswordConfirmation() throws Exception {
        final ModelAndView modelAndView = mockMvc.perform(post("/register")
                .flashAttr("user", USER)
                .param("confirmPassword", "pass"))
                .andExpect(view().name("register"))
                .andReturn().getModelAndView();

        final Map<String, Object> model = Objects.requireNonNull(modelAndView).getModel();

        assertThat(model.get("confirmationError"), is(true));
    }
}