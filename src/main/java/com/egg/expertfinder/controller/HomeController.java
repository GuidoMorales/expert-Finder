package com.egg.expertfinder.controller;

import com.egg.expertfinder.entity.CustomUser;
import com.egg.expertfinder.exception.MyException;
import com.egg.expertfinder.service.ProfessionalService;
import com.egg.expertfinder.service.UserService;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/")  // localhost:8080/
public class HomeController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private ProfessionalService professionalService;

    @GetMapping("/")
    public String index() {
        return "index.html";
    }

    @GetMapping("/login")  // localhost:8080/login
    public String login(@RequestParam(required = false) String error, ModelMap modelo) {
        if (error != null) {
            modelo.put("error", "Email o Contraseña invalidos!");
        }
        return "login.html";
    }

    @GetMapping("/register-user")  // localhost:8080/register
    public String registerUser() {
        return "user_form.html";
    }

    @PostMapping("/register-user")
    public String registerUser(@RequestParam String name, @RequestParam String lastName,
            @RequestParam String email, @RequestParam String password, @RequestParam String password2,
            @RequestParam String countryKey, @RequestParam String country, 
            @RequestParam String address, @RequestParam MultipartFile file, ModelMap model) {
        try {
            userService.createUser(name, lastName, email, password, password2, countryKey,
                country, address, file);
            model.put("exito", "Usuario registrado.");
            return "redirect:/login";
        } catch (MyException e) {
            model.put("error", e.getMessage());
            return "user_form.html";
        }
    }
    
    @GetMapping("/register-professional")  // localhost:8080/register
    public String registerProfessional() {
        return "professional_form.html";
    }

    @PostMapping("/register-professional")
    public String registerProfessional(@RequestParam String name, @RequestParam String lastName,
            @RequestParam String email, @RequestParam String password, @RequestParam String password2, 
            @RequestParam String address, @RequestParam MultipartFile file, ModelMap model) {
        try {
            professionalService.createProfessional(name, lastName, email, password, password2, 
                    address, file, email, lastName, name);
            model.put("exito", "Usuario registrado.");
            return "redirect:/login";
        } catch (MyException e) {
            model.put("error", e.getMessage());
            return "user_form.html";
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping("/home")
    public String home(HttpSession session) {

        CustomUser userLogin = (CustomUser) session.getAttribute("usersession");

        if (userLogin.getRole().toString().equals("ADMIN")) {
            return "redirect:/admin/dashboard";
        } else {
            return "home.html";
        }

    }

}
