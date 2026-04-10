package com.ems.controller;

import com.ems.dto.UserDTO;
import com.ems.model.Department;
import com.ems.model.Role;
import com.ems.model.User;
import com.ems.service.DepartmentService;
import com.ems.service.EmployeeService;
import com.ems.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final EmployeeService employeeService;
    private final DepartmentService departmentService;

    public AdminController(UserService userService,
                           EmployeeService employeeService,
                           DepartmentService departmentService) {
        this.userService = userService;
        this.employeeService = employeeService;
        this.departmentService = departmentService;
    }

    // User Management
    @GetMapping("/users")
    public String manageUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/manage-users";
    }

    @GetMapping("/users/new")
    public String newUserForm(Model model) {
        model.addAttribute("userDTO", new UserDTO());
        model.addAttribute("roles", Role.values());
        return "admin/user-form";
    }

    @PostMapping("/users/save")
    public String saveUser(@Valid @ModelAttribute UserDTO userDTO,
                           BindingResult result,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("roles", Role.values());
            return "admin/user-form";
        }

        try {
            if (userDTO.getId() == null) {
                userService.createUser(userDTO);
                redirectAttributes.addFlashAttribute("message", "User created successfully");
            } else {
                userService.updateUser(userDTO.getId(), userDTO);
                redirectAttributes.addFlashAttribute("message", "User updated successfully");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/users";
    }

    @GetMapping("/users/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().name());
        dto.setEnabled(user.isEnabled());

        model.addAttribute("userDTO", dto);
        model.addAttribute("roles", Role.values());
        return "admin/user-form";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("message", "User deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Cannot delete user");
        }
        return "redirect:/admin/users";
    }

    // Department Management
    @GetMapping("/departments")
    public String manageDepartments(Model model) {
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("department", new Department());
        return "admin/manage-departments";
    }

    @PostMapping("/departments/save")
    public String saveDepartment(@Valid @ModelAttribute Department department,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Invalid department data");
            return "redirect:/admin/departments";
        }

        try {
            if (department.getId() == null) {
                departmentService.createDepartment(department);
                redirectAttributes.addFlashAttribute("message", "Department created successfully");
            } else {
                departmentService.updateDepartment(department.getId(), department);
                redirectAttributes.addFlashAttribute("message", "Department updated successfully");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/departments";
    }

    @GetMapping("/departments/delete/{id}")
    public String deleteDepartment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            departmentService.deleteDepartment(id);
            redirectAttributes.addFlashAttribute("message", "Department deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Cannot delete department with employees");
        }
        return "redirect:/admin/departments";
    }
}
