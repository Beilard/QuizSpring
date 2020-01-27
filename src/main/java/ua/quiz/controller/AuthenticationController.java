package ua.quiz.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ua.quiz.model.dto.LoginForm;
import ua.quiz.model.dto.User;
import ua.quiz.model.service.UserService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Objects;

@Controller
@RequestMapping("/")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class AuthenticationController {
    private final UserService userService;

    @GetMapping("/")
    public String main(){
        return "index";
    }

    @GetMapping(value = {"/login"})
    public String login(Model model, @Valid LoginForm loginForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "login";
        }
        model.addAttribute("user", loginForm.getEmail());
        return "/player/player-page";
    }

    @GetMapping("/register")
    public ModelAndView register() {
        ModelAndView modelAndView = new ModelAndView("register");
        modelAndView.addObject("user", new User());
        return modelAndView;
    }

    @PostMapping("/register")
    public ModelAndView registerUser(@Valid User user, BindingResult bindingResult,
                                     @RequestParam(name = "confirmPassword") String confirmPass) {
        ModelAndView modelAndView = new ModelAndView();

        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("register");
        } else if (!Objects.equals(user.getPassword(), confirmPass)) {
            modelAndView.addObject("confirmationError", true);
            modelAndView.setViewName("register");
        } else {
            userService.register(user);
            modelAndView.setViewName("redirect:/");
        }
        return modelAndView;
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

}
