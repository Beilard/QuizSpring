package ua.quiz.controller;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.quiz.model.dto.*;
import ua.quiz.model.exception.EntityAlreadyExistsException;
import ua.quiz.model.exception.EntityNotFoundException;
import ua.quiz.model.service.GameService;
import ua.quiz.model.service.PhaseService;
import ua.quiz.model.service.TeamService;
import ua.quiz.model.service.UserService;

import javax.servlet.http.HttpSession;
import java.util.List;

@Log4j
@Controller
@RequestMapping("/player")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class PlayerController {
    private static final int DEFAULT_SIZE_PAGE = 10;

    private UserService userService;
    private TeamService teamService;
    private GameService gameService;
    private PhaseService phaseService;

    @GetMapping
    public String main(HttpSession session) {
        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        session.setAttribute("user", user);

        return "player-page";
    }

    @GetMapping("/create-team")
    public String showCreateTeam() {
        return "create-team";
    }

    @GetMapping("/form-team")
    public String createTeam(Model model, HttpSession session, @RequestParam("teamName") String teamName) {
        try {
            teamService.createTeam(teamName);
        } catch (EntityAlreadyExistsException | IllegalArgumentException e) {
            log.info("Team name passed was either null of taken");
            model.addAttribute("nameTaken", true);
            return "create-team";
        }
        final Team formedTeam = teamService.findTeamByName(teamName);
        User user = (User) session.getAttribute("user");

        User userWithPassword = userService.findByEmail(user.getEmail());

        userService.update(userWithPassword.toBuilder()
                .isCaptain(true)
                .team(formedTeam)
                .build());

        session.setAttribute("user", removePassword(userService.findByEmail(user.getEmail())));
        return "player-page";
    }

    @GetMapping("/join-team")
    public String joinTeam(Model model, HttpSession session, @RequestParam("teamNameForJoin") String teamNameForJoin) {
        final User user = (User) session.getAttribute("user");

        try {
            teamService.joinTeam(user, teamService.findTeamByName(teamNameForJoin).getId());
            session.setAttribute("user", removePassword(userService.findByEmail(user.getEmail())));
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            log.info("Failed to join");
            model.addAttribute("nameDoesNotExist", true);
            return "create-team";
        }

        return "player-page";
    }


    @GetMapping("/check-team")
    public String checkTeam(Model model, HttpSession session) {
        final User user = (User) session.getAttribute("user");

        try {
            List<User> usersOfTeam = userService.findByTeamId(user.getTeam().getId());
            model.addAttribute("usersOfTeam", usersOfTeam);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            log.info("Failed to create user list");
            return "/player";
        }

        return "team-page";
    }

    @GetMapping("/change-captains")
    public String changeCaptains(HttpSession session,
                                 @RequestParam(name = "newCaptainEmail") String newCaptainEmail) {
        final User user = (User) session.getAttribute("user");
        try {
            teamService.changeCaptain(userService.findByEmail(newCaptainEmail), user);
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            log.info("User " + user + " tried to change captaincy with an invalid argument");
            return "player-page";
        }

        final User userWithPassword = userService.findByEmail(user.getEmail());

        session.setAttribute("user", removePassword(userWithPassword));
        return "player-page";
    }

    @GetMapping("/leave-team")
    public String leaveTeam(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user.getIsCaptain()) {
            model.addAttribute("isCaptainText", true);
            return "team-page";
        }
        final User userWithPassword = userService.findByEmail(user.getEmail());
        teamService.leaveTeam(userWithPassword);

        final User updatedUser = userService.findByEmail(user.getEmail());
        session.setAttribute("user", removePassword(updatedUser));

        return "player-page";
    }

    @GetMapping("/configure-game")
    public String configureGame(Model model, HttpSession session,
                                @RequestParam(value = "numberOfQuestions", defaultValue = "10") Integer numberOfQuestions,
                                @RequestParam(value = "timePerQuestion", defaultValue = "60") Integer timePerQuestion) {
        final Team userTeam = ((User) (session.getAttribute("user"))).getTeam();
        final Game game = gameService.startGame(userTeam, numberOfQuestions, timePerQuestion);
        phaseService.generatePhaseList(game, numberOfQuestions);

        session.setAttribute("game", game);
        return "forward:/player/generate-phase";
    }

    @GetMapping("/show-configuration")
    public String showConfiguration() {
        return "configuration-page";
    }

    @GetMapping("/generate-phase")
    public String generatePhase(Model model, HttpSession session) {
        final Game game = (Game) session.getAttribute("game");
        final List<Phase> phases = phaseService.findPhasesByGameId(game.getId());

        final Phase currentPhase = phases.get(game.getCurrentPhase());
        phaseService.initiatePhase(currentPhase, game.getTimePerQuestion());
        Game modifiedGame = gameService.findById(game.getId());
        session.setAttribute("game", modifiedGame);
        session.setAttribute("question", getQuestion(game, phases));
        model.addAttribute("hintUsed", false);

        return "game-page";
    }

    @GetMapping("/finish-phase")
    public String finishPhase(Model model, HttpSession session,
                              @RequestParam(value = "givenAnswer") String givenAnswer) {
        final Game game = (Game) session.getAttribute("game");

        if (givenAnswer == null) {
            return "game-page";
        }

        final Integer currentPhase = game.getCurrentPhase();

        final List<Phase> phases = phaseService.findPhasesByGameId(game.getId());

        phaseService.finishPhase(phases.get(currentPhase), givenAnswer);

        game.setCurrentPhase(currentPhase + 1);
        gameService.updateGame(game);

        session.setAttribute("game", gameService.findById(game.getId()));

        if (currentPhase >= game.getNumberOfQuestions() - 1) {
            return "forward:/player/finish-game";
        } else {
            return "forward:/player/generate-phase";
        }
    }

    @GetMapping("/finish-game")
    public String finishGame(Model model, HttpSession session) {
        Game game = (Game) session.getAttribute("game");
        gameService.finishGame(game);
        session.removeAttribute("game");
        return "player-page";
    }

    @GetMapping("provide-hint")
    public String provideHint(Model model, HttpSession session) {
        final Game game = (Game) session.getAttribute("game");
        final List<Phase> phases = phaseService.findPhasesByGameId(game.getId());

        phaseService.useHint(phases.get(game.getCurrentPhase()));

        session.setAttribute("game", gameService.findById(game.getId()));
        model.addAttribute("hintUsed", true);

        return "game-page";
    }

    @GetMapping("/statistics")
    public String getStatistics(Model model, HttpSession session,
                                @RequestParam(value = "joinGameId") Long gameId) {
        Game game = gameService.findById(gameId);

        if (game.getStatus() != Status.REVIEWED) {
            return "player-page";
        }
        Long correctAnswersCount = gameService.getCorrectAnswersCount(game);

        session.setAttribute("correctAnswersCount", correctAnswersCount);
        session.setAttribute("numberOfQuestions", game.getNumberOfQuestions());

        return "statistics-page";
    }

    @GetMapping("join-game")
    public String joinGame(Model model, HttpSession session,
                           @RequestParam(value = "joinGameId") Long gameId) {
        final User user = (User) session.getAttribute("user");
        Game foundGame;

        try {
            foundGame = gameService.findById(gameId);
        } catch (EntityNotFoundException e) {
            log.info("Game with ID " + gameId + "not found");
            return "player-page";
        }

        if (foundGame.getStatus() == Status.REVIEWED) {
            return "forward:/statistics";
        } else if (foundGame.getStatus() == Status.PENDING) {
            return "player-page";
        }

        if (!foundGame.getTeam().getId().equals(user.getTeam().getId())) {
            log.info("User tried to join not his team's game. User ID: " + user.getId());
            return "player-page";
        }

        final List<Phase> phases = phaseService.findPhasesByGameId(foundGame.getId());
        session.setAttribute("game", foundGame);
        session.setAttribute("question", getQuestion(foundGame, phases));
        model.addAttribute("hintUsed", getQuestion(foundGame, phases).getHint());

        return "game-page";
    }

    @GetMapping("/show-team-games")
    public String viewAllGames(@PageableDefault(size = DEFAULT_SIZE_PAGE, sort = {"id"},
            direction = Sort.Direction.DESC) Pageable pageable, Model model, HttpSession session) {
        final User user = (User) session.getAttribute("user");

        Page<Game> allTeamGames = gameService.findAllByTeamId(user.getTeam().getId(), pageable);

        model.addAttribute("allTeamGames", allTeamGames);
        return "view-all-team-games";
    }

    private User removePassword(User user) {
        return user.toBuilder()
                .password(null)
                .build();
    }

    private Question getQuestion(Game game, List<Phase> phases) {
        return phases.get(game.getCurrentPhase()).getQuestion();
    }
}
