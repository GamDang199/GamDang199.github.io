package com.example.demo.controller;

import com.example.demo.model.Account;
import com.example.demo.model.User;
import com.example.demo.service.User.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@SessionAttributes("money")
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("/register")
    public String register(Model model){
        model.addAttribute("userForm",new User());
        return "register";
    }

    @PostMapping("/register")
    public String save(@ModelAttribute("userForm") @Valid User userForm, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes){
        if(bindingResult.hasErrors()){
            return "register";
        }
        if(!userService.checkPassword(userForm.getPassword(),userForm.getPasswordcf())){
            model.addAttribute("errMsg","Password confirm is wrong");
            return "register";
        }
        if(userService.existsByUsername(userForm.getUsername())){
            model.addAttribute("errMsg","User exists!");
            return "register";
        }
        model.addAttribute("succMsg","Register User Success!");
        userService.save(userForm);
        return "register";
    }

    @GetMapping("/login")
    public String login(Model model, HttpSession session){
        if(session.getAttribute("USER")!=null){
            return "redirect:/";
        }
        model.addAttribute("userForm",new Account());
        return "login";
    }
    @PostMapping("/login")
    public String postLogin(@Valid @ModelAttribute("userForm") Account userForm, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model, HttpSession session){
        if(bindingResult.hasErrors()){
            return "login";
        }
        int id = userService.checkLogin(userForm.getUsername(),userForm.getPassword());
        if(id!=0){
            session.setAttribute("USER",userForm.getUsername());
            session.setAttribute("ID",id);
            return "redirect:/";
        }
        model.addAttribute("errMsg","Invalid username or password!");
        return "login";
    }
}
