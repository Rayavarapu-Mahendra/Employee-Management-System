package com.ems.controller;

import com.ems.dto.EmployeeDTO;
import com.ems.model.Employee;
import com.ems.model.User;
import com.ems.service.DepartmentService;
import com.ems.service.EmployeeService;
import com.ems.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/employee")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final UserService userService;
    private final DepartmentService departmentService;

    public EmployeeController(EmployeeService employeeService,
                              UserService userService,
                              DepartmentService departmentService) {
        this.employeeService = employeeService;
        this.userService = userService;
        this.departmentService = departmentService;
    }

    @GetMapping("/profile")
    public String viewProfile(Authentication authentication, Model model) {
        User user = userService.getUserByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Employee employee = employeeService.getEmployeeByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Employee profile not found"));

        model.addAttribute("employee", employee);
        model.addAttribute("employeeDTO", employeeService.toDTO(employee));
        return "employee/profile";
    }

    @GetMapping("/profile/edit")
    public String editProfile(Authentication authentication, Model model) {
        User user = userService.getUserByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Employee employee = employeeService.getEmployeeByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Employee profile not found"));

        model.addAttribute("employeeDTO", employeeService.toDTO(employee));
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "employee/edit-profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@Valid @ModelAttribute EmployeeDTO employeeDTO,
                                BindingResult result,
                                Authentication authentication,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("departments", departmentService.getAllDepartments());
            return "employee/edit-profile";
        }

        try {
            User user = userService.getUserByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Employee employee = employeeService.getEmployeeByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Employee not found"));

            // Employees can only update limited fields
            Employee existing = employeeService.getEmployeeById(employee.getId()).get();
            employeeDTO.setId(existing.getId());
            employeeDTO.setSalary(existing.getSalary()); // Prevent salary modification
            employeeDTO.setDesignation(existing.getDesignation()); // Prevent designation modification

            employeeService.updateEmployee(employee.getId(), employeeDTO);
            redirectAttributes.addFlashAttribute("message", "Profile updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/employee/profile";
    }

    @GetMapping("/change-password")
    public String changePasswordForm() {
        return "employee/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "New passwords do not match");
            return "redirect:/employee/change-password";
        }

        try {
            User user = userService.getUserByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            userService.updatePassword(user.getId(), currentPassword, newPassword);
            redirectAttributes.addFlashAttribute("message", "Password changed successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/employee/profile";
    }
}
