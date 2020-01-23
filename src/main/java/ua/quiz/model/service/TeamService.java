package ua.quiz.model.service;

import ua.quiz.model.dto.Team;
import ua.quiz.model.dto.User;

public interface TeamService {
    void createTeam(String teamName);

    Team findTeamByName(String name);

    void joinTeam(User user, Long teamId);

    void leaveTeam(User user);

    void changeCaptain(User newCaptain, User oldCaptain);
}
