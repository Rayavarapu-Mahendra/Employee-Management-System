package com.ems.controller;

import com.ems.dto.EmployeeDTO;
import com.ems.model.Employee;
import com.ems.model.Role;
import com.ems.service.DepartmentService;
import com.ems.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/hr")
public class HRController {

    private final EmployeeService employeeService;
    private final DepartmentService departmentService;

    public HRController(EmployeeService employeeService, DepartmentService departmentService) {
        this.employeeService = employeeService;
        this.departmentService = departmentService;
    }

    @GetMapping("/employees")
    public String listEmployees(@RequestParam(required = false) String search, Model model) {
        if (search != null && !search.isEmpty()) {
            model.addAttribute("employees", employeeService.searchEmployees(search));
            model.addAttribute("search", search);
        } else {
            model.addAttribute("employees", employeeService.getAllEmployees());
        }
        return "hr/employees";
    }

    @GetMapping("/employees/new")
    public String newEmployeeForm(Model model) {
        model.addAttribute("employeeDTO", new EmployeeDTO());
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("roles", Role.values());
        return "hr/employee-form";
    }

    @PostMapping("/employees/save")
    public String saveEmployee(@Valid @ModelAttribute EmployeeDTO employeeDTO,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("departments", departmentService.getAllDepartments());
            model.addAttribute("roles", Role.values());
            return "hr/employee-form";
        }

        try {
            if (employeeDTO.getId() == null) {
                employeeService.createEmployee(employeeDTO);
                redirectAttributes.addFlashAttribute("message", "Employee created successfully");
            } else {
                employeeService.updateEmployee(employeeDTO.getId(), employeeDTO);
                redirectAttributes.addFlashAttribute("message", "Employee updated successfully");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            model.addAttribute("departments", departmentService.getAllDepartments());
            model.addAttribute("roles", Role.values());
            return "hr/employee-form";
        }

        return "redirect:/hr/employees";
    }

    @GetMapping("/employees/edit/{id}")
    public String editEmployeeForm(@PathVariable Long id, Model model) {
        Employee employee = employeeService.getEmployeeById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        model.addAttribute("employeeDTO", employeeService.toDTO(employee));
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("roles", Role.values());
        return "hr/employee-form";
    }

    @GetMapping("/employees/view/{id}")
    public String viewEmployee(@PathVariable Long id, Model model) {
        Employee employee = employeeService.getEmployeeById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        model.addAttribute("employee", employee);
        return "hr/employee-view";
    }

    @GetMapping("/employees/delete/{id}")
    public String deleteEmployee(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            employeeService.deleteEmployee(id);
            redirectAttributes.addFlashAttribute("message", "Employee deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Cannot delete employee");
        }
        return "redirect:/hr/employees";
    }
}
