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
import ua.quiz.model.dto.Game;
import ua.quiz.model.dto.Phase;
import ua.quiz.model.dto.Question;
import ua.quiz.model.dto.User;
import ua.quiz.model.exception.EntityNotFoundException;
import ua.quiz.model.service.GameService;
import ua.quiz.model.service.PhaseService;

import javax.servlet.http.HttpSession;
import java.util.List;

@Log4j
@Controller
@RequestMapping("/judge")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class JudgeController {
    private static final int DEFAULT_SIZE_PAGE = 10;

    private GameService gameService;
    private PhaseService phaseService;

    @GetMapping
    public String main(HttpSession session) {
        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        session.setAttribute("user", user);

        return "player-page";
    }

    @GetMapping("/start-review")
    public String checkTeam(HttpSession session,
                            @RequestParam(value = "gameIdToReview") Long gameIdToReview) {
        Game gamePreparedForReview;

        try {
            gamePreparedForReview = gameService.startReview(gameService.findById(gameIdToReview));
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            log.info("Game ID passed wasn't found");
            return "forward:/judge/view-all-games";
        }
        session.setAttribute("gameForReview", gamePreparedForReview);

        return "forward:/judge/prepare-phase";
    }

    @GetMapping("/prepare-phase")
    public String preparePhase(HttpSession session, Model model) {
        final Game game = (Game) session.getAttribute("gameForReview");
        final List<Phase> phases = phaseService.findPhasesByGameId(game.getId());

        model.addAttribute("reviewedQuestion", getQuestion(game, phases));
        model.addAttribute("reviewedPhase", getCurrentPhase(game, phases));

        return "review-page";
    }

    @GetMapping("/right-answer")
    public String rightAnswer(HttpSession session) {
        Game game = (Game) session.getAttribute("gameForReview");
        final Integer currentPhase = game.getCurrentPhase();
        final List<Phase> phases = phaseService.findPhasesByGameId(game.getId());

        phaseService.reviewPhasePositively(phases.get(currentPhase));

        game.setCurrentPhase(currentPhase + 1);
        gameService.updateGame(game);

        session.setAttribute("gameForReview", gameService.findById(game.getId()));
        if (currentPhase >= game.getNumberOfQuestions() - 1) {
            return "forward:/judge/finish-review";
        } else {
            return "forward:/judge/prepare-phase";
        }
    }

    @GetMapping("/wrong-answer")
    public String wrongAnswer(HttpSession session) {
        Game game = (Game) session.getAttribute("gameForReview");
        final Integer currentPhase = game.getCurrentPhase();

        game.setCurrentPhase(currentPhase + 1);
        gameService.updateGame(game);

        session.setAttribute("gameForReview", gameService.findById(game.getId()));
        if (currentPhase >= game.getNumberOfQuestions() - 1) {
            return "forward:/judge/finish-review";
        } else {
            return "forward:/judge/prepare-phase";
        }
    }

    @GetMapping("/finish-review")
    public String finishReview(HttpSession session) {
        final Game reviewedGame = (Game) session.getAttribute("gameForReview");

        gameService.finishReview(reviewedGame);
        session.removeAttribute("reviewedGame");

        return "player-page";

    }

    @GetMapping("/view-all-games")
    public String viewAllGames(@PageableDefault(size = DEFAULT_SIZE_PAGE, sort = {"id"},
            direction = Sort.Direction.DESC) Pageable pageable, Model model, HttpSession session) {
        Page<Game> allGames = gameService.findAll(pageable);

        model.addAttribute("allGames", allGames);
        return "view-all-games";
    }

    private Phase getCurrentPhase(Game game, List<Phase> phases) {
        return phases.get(game.getCurrentPhase());
    }

    private Question getQuestion(Game game, List<Phase> phases) {
        return getCurrentPhase(game, phases).getQuestion();
    }
}
