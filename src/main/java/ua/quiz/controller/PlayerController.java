package ua.quiz.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ua.quiz.model.dto.Team;
import ua.quiz.model.dto.User;
import ua.quiz.model.service.TeamService;
import ua.quiz.model.service.UserService;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/player")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class PlayerController {
    private UserService userService;
    private TeamService teamService;

    @GetMapping
    public String main(HttpSession session) {
        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        session.setAttribute("user", user);

        return "player-page";
    }

    @GetMapping("/create-team")
    public String showCreateTeam(){
        return "create-team";
    }


    public ModelAndView createTeam(HttpSession session, @RequestParam("teamName") String teamName) {
        final ModelAndView modelAndView = new ModelAndView("profile-page");
        teamService.createTeam(teamName);

        final Team formedTeam = teamService.findTeamByName(teamName);

        User user = (User) session.getAttribute("user");

        userService.update(user.toBuilder()
                .isCaptain(true)
                .team(formedTeam)
                .build());

        session.setAttribute("user", userService.findByEmail(user.getEmail()));
        return modelAndView;
    }

    public ModelAndView joinTeam(HttpSession session, @RequestParam("teamNameForJoin") String teamNameForJoin) {
        final ModelAndView modelAndView = new ModelAndView("profile-page");
        User user = (User) session.getAttribute("user");
        teamService.joinTeam(user, teamService.findTeamByName(teamNameForJoin).getId());
        session.setAttribute("user", userService.findByEmail(user.getEmail()));

        return modelAndView;
    }

    public ModelAndView leaveTeam(HttpSession session) {
        final ModelAndView modelAndView = new ModelAndView("profile-page");


        return modelAndView;
    }
}
