package com.ems.controller;

import com.ems.model.Employee;
import com.ems.model.User;
import com.ems.service.EmployeeService;
import com.ems.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Controller
public class AuthController {

    private final UserService userService;
    private final EmployeeService employeeService;

    public AuthController(UserService userService, EmployeeService employeeService) {
        this.userService = userService;
        this.employeeService = employeeService;
    }

    @GetMapping(value={"/login" , "", "/"})
    public String loginPage() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        String email = authentication.getName();
        Optional<User> userOpt = userService.getUserByEmail(email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            model.addAttribute("user", user);

            Optional<Employee> employeeOpt = employeeService.getEmployeeByUserId(user.getId());
            employeeOpt.ifPresent(emp -> model.addAttribute("employee", emp));
        }

        // Determine role for dashboard content
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            model.addAttribute("role", "ADMIN");
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_HR"))) {
            model.addAttribute("role", "HR");
        } else {
            model.addAttribute("role", "EMPLOYEE");
        }

        return "dashboard";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
}
