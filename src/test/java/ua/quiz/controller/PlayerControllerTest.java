package ua.quiz.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ua.quiz.configuration.LoginSuccessHandler;
import ua.quiz.model.service.GameService;
import ua.quiz.model.service.PhaseService;
import ua.quiz.model.service.TeamService;
import ua.quiz.model.service.UserService;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@WebMvcTest(PlayerController.class)
@WithMockUser(username = "user@gmail.com", authorities = "PLAYER")
public class PlayerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;
    @MockBean
    private TeamService teamService;
    @MockBean
    private GameService gameService;
    @MockBean
    private PhaseService phaseService;
    @MockBean
    private LoginSuccessHandler loginSuccessHandler;

    @Test
    public void showCreateTeam() {
    }

    @Test
    public void createTeam() {
    }

    @Test
    public void joinTeam() {
    }

    @Test
    public void checkTeam() {
    }

    @Test
    public void changeCaptains() {
    }

    @Test
    public void leaveTeam() {
    }

    @Test
    public void configureGame() {
    }

    @Test
    public void showConfiguration() {
    }

    @Test
    public void generatePhase() {
    }

    @Test
    public void finishPhase() {
    }

    @Test
    public void finishGame() {
    }

    @Test
    public void provideHint() {
    }

    @Test
    public void getStatistics() {
    }

    @Test
    public void joinGame() {
    }
}