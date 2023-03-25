package com.example.burger_restaurant.controller;

import com.example.burger_restaurant.domain.Role;
import com.example.burger_restaurant.domain.User;
import com.example.burger_restaurant.repos.UserRepo;
import com.example.burger_restaurant.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Collections;
import java.util.Map;

@Controller
public class RegistrationController {
    @Autowired
    private UserRepo userRepo;

    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(User user, Map<String, Object> model) {
        UserService userService = new UserService();
        String result = userService.addUser(user);

        if (result.isEmpty()) {
            return "redirect:/login";
        }
        else {
            model.put("message", result);
            return "registration";
        }
    }
}
