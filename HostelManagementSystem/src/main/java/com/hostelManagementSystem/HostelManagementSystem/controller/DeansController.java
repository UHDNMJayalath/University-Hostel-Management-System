package com.hostelManagementSystem.HostelManagementSystem.controller;

import com.hostelManagementSystem.HostelManagementSystem.entity.Student;
import com.hostelManagementSystem.HostelManagementSystem.repository.HostelRepository;
import com.hostelManagementSystem.HostelManagementSystem.repository.StudentRepository;
import com.hostelManagementSystem.HostelManagementSystem.service.DeansService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
public class DeansController {

    @Autowired
    private StudentRepository studentRepo;

    @Autowired
    private HostelRepository hostelRepo;

    @Autowired
    private DeansService deansService;

    @GetMapping("/Deans-dashboard")
    public String showDeanDashboard(@RequestParam(required = false) String q,
                                    Model model,
                                    Principal principal) {

        String deansEmail = principal.getName();                       // Logged-in dean's email
        String faculty = deansService.getFacultyFromDeanEmail(deansEmail); // Map to faculty

        // Pass the faculty name to the view (optional)
        model.addAttribute("faculty", faculty);

        if (q != null && !q.isEmpty()) {
            Optional<Student> studentOpt = studentRepo.findByStudentIdAndFaculty(q, faculty);
            if (studentOpt.isPresent()) {
                model.addAttribute("student", studentOpt.get());
            } else {
                model.addAttribute("error", "Student not found in your faculty");
            }
        }

        // List all hostels related to the dean's faculty
        model.addAttribute("hostels", hostelRepo.findByFaculty(faculty));

        // Optional: you can also list all students in that faculty
        List<Student> studentsInFaculty = studentRepo.findByFaculty(faculty);
        model.addAttribute("students", studentsInFaculty);

        return "Deans-dashboard";
    }
}
