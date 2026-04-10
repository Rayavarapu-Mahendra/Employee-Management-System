package com.ems;

import com.ems.model.Department;
import com.ems.model.Employee;
import com.ems.model.Role;
import com.ems.model.User;
import com.ems.repository.DepartmentRepository;
import com.ems.repository.EmployeeRepository;
import com.ems.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@SpringBootApplication
public class EmployeeManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmployeeManagementSystemApplication.class, args);
    }

    @Bean
    CommandLineRunner initData(UserRepository userRepository,
                               EmployeeRepository employeeRepository,
                               DepartmentRepository departmentRepository,
                               PasswordEncoder passwordEncoder) {
        return args -> {
            // Create default departments if not exist
            if (departmentRepository.count() == 0) {
                departmentRepository.save(new Department(null, "IT", "Information Technology", null));
                departmentRepository.save(new Department(null, "HR", "Human Resources", null));
                departmentRepository.save(new Department(null, "Finance", "Finance Department", null));
                departmentRepository.save(new Department(null, "Marketing", "Marketing Department", null));
            }

            // Create admin user if not exist
            if (!userRepository.existsByEmail("admin@ems.com")) {
                User admin = new User();
                admin.setEmail("admin@ems.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(Role.ADMIN);
                admin.setEnabled(true);
                userRepository.save(admin);
                System.out.println("Admin user created: admin@ems.com / admin123");
            }

            // Create HR user if not exist
            if (!userRepository.existsByEmail("hr@ems.com")) {
                User hrUser = new User();
                hrUser.setEmail("hr@ems.com");
                hrUser.setPassword(passwordEncoder.encode("hr123"));
                hrUser.setRole(Role.HR);
                hrUser.setEnabled(true);
                hrUser = userRepository.save(hrUser);

                Department hrDept = departmentRepository.findByName("HR").orElse(null);
                Employee hrEmployee = new Employee();
                hrEmployee.setFirstName("HR");
                hrEmployee.setLastName("Manager");
                hrEmployee.setPhone("1234567890");
                hrEmployee.setDesignation("HR Manager");
                hrEmployee.setSalary(75000.0);
                hrEmployee.setJoiningDate(LocalDate.now());
                hrEmployee.setUser(hrUser);
                hrEmployee.setDepartment(hrDept);
                employeeRepository.save(hrEmployee);
                System.out.println("HR user created: hr@ems.com / hr123");
            }

            // Create sample employee if not exist
            if (!userRepository.existsByEmail("john@ems.com")) {
                User empUser = new User();
                empUser.setEmail("john@ems.com");
                empUser.setPassword(passwordEncoder.encode("emp123"));
                empUser.setRole(Role.EMPLOYEE);
                empUser.setEnabled(true);
                empUser = userRepository.save(empUser);

                Department itDept = departmentRepository.findByName("IT").orElse(null);
                Employee employee = new Employee();
                employee.setFirstName("John");
                employee.setLastName("Doe");
                employee.setPhone("9876543210");
                employee.setAddress("123 Main Street");
                employee.setDesignation("Software Developer");
                employee.setSalary(60000.0);
                employee.setJoiningDate(LocalDate.now().minusMonths(6));
                employee.setUser(empUser);
                employee.setDepartment(itDept);
                employeeRepository.save(employee);
                System.out.println("Employee user created: john@ems.com / emp123");
            }
        };
    }
}
