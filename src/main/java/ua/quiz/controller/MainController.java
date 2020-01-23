package ua.quiz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ua.quiz.model.entity.UserEntity;
import ua.quiz.model.repository.UserRepository;

@Controller
@RequestMapping(path = "/demo")
public class MainController {
    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    @PostMapping(path = "/add")
    public @ResponseBody String addNewUser(@RequestParam String name, @RequestParam String email) {
        UserEntity userEntity = new UserEntity();
        userRepository.save(userEntity);
        return "saved";
    }
}
