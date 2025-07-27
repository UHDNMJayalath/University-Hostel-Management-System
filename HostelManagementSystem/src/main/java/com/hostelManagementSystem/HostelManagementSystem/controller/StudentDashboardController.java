package com.hostelManagementSystem.HostelManagementSystem.controller;

import com.hostelManagementSystem.HostelManagementSystem.entity.Damage;
import com.hostelManagementSystem.HostelManagementSystem.entity.Payment;
import com.hostelManagementSystem.HostelManagementSystem.entity.Residence;
import com.hostelManagementSystem.HostelManagementSystem.entity.Student;
import com.hostelManagementSystem.HostelManagementSystem.repository.DamageRepository;
import com.hostelManagementSystem.HostelManagementSystem.repository.PaymentRepository;
import com.hostelManagementSystem.HostelManagementSystem.repository.ResidenceRepository;
import com.hostelManagementSystem.HostelManagementSystem.repository.StudentRepository;
import com.hostelManagementSystem.HostelManagementSystem.service.GoogleDriveService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
public class StudentDashboardController {

    @Autowired
    private StudentRepository studentRepository;

    @GetMapping("/student-dashboard")
    public String dashboard(HttpSession session, Model model) {

        String email = (String) session.getAttribute("loggedInUserEmail");

        if (email == null) {

            return "redirect:/login";
        }

        Optional<Student> studentOptional = studentRepository.findByEmailIgnoreCase(email);

        if (studentOptional.isPresent()) {
            model.addAttribute("student", studentOptional.get());
            return "/student-dashboard";
        } else {

            return "error";
        }
    }




    @Autowired
    private ResidenceRepository residenceRepository;

    @GetMapping("/Student_History_Residence")
    public String studentHistory(HttpSession session, Model model) {
        String email = (String) session.getAttribute("loggedInUserEmail");

        if (email == null) {
            return "redirect:/login";
        }

        Optional<Student> studentOptional = studentRepository.findByEmailIgnoreCase(email);
        if (studentOptional.isPresent()) {
            Student student = studentOptional.get();
            model.addAttribute("student", student);

            List<Residence> residenceList = residenceRepository.findByStudent(student);
            model.addAttribute("residences", residenceList); // ✅ Data pass

            return "Student_History_Residence";
        } else {
            return "error";
        }
    }



    @GetMapping("Student_ComplainsAndRequests")
    public String studentComplaints(HttpSession session, Model model) {
        String email = (String) session.getAttribute("loggedInUserEmail");

        if (email == null) {
            return "redirect:/login";
        }

        Optional<Student> studentOptional = studentRepository.findByEmailIgnoreCase(email);
        if (studentOptional.isPresent()) {
            model.addAttribute("student", studentOptional.get());
            return "Student_ComplainsAndRequests"; // <-- Thymeleaf HTML file name
        } else {
            return "error";
        }
    }


    @Autowired
    private PaymentRepository paymentRepository;


    @GetMapping("/Student_History_Payment")
    public String paymentPage(HttpSession session, Model model) {
        String email = (String) session.getAttribute("loggedInUserEmail");

        if (email == null) {
            return "redirect:/login";
        }

        Optional<Student> studentOptional = studentRepository.findByEmailIgnoreCase(email);
        if (studentOptional.isPresent()) {
            Student student = studentOptional.get();
            model.addAttribute("student", student);

            List<Payment> payments = paymentRepository.findByStudent(student);
            model.addAttribute("payments", payments); // <-- IMPORTANT!

            return "Student_History_Payment";
        } else {
            return "error";
        }
    }



    @Autowired
    private DamageRepository damageRepository;

    @GetMapping("/Student_History_Damage")
    public String damagePage(HttpSession session, Model model) {
        String email = (String) session.getAttribute("loggedInUserEmail");

        if (email == null) {
            return "redirect:/login";
        }

        Optional<Student> studentOptional = studentRepository.findByEmailIgnoreCase(email);
        if (studentOptional.isPresent()) {
            Student student = studentOptional.get();
            model.addAttribute("student", student);

            List<Damage> damages = damageRepository.findByStudent(student);
            model.addAttribute("damages", damages); // ✅ send to view

            return "Student_History_Damage";
        } else {
            return "error";
        }
    }

    @GetMapping("/Student_Payments")
    public String studentPaymentForm(HttpSession session, Model model) {
        String email = (String) session.getAttribute("loggedInUserEmail");

        if (email == null) {
            return "redirect:/login";
        }

        Optional<Student> studentOptional = studentRepository.findByEmailIgnoreCase(email);
        if (studentOptional.isPresent()) {
            model.addAttribute("student", studentOptional.get());
            return "Student_Payments"; // <-- Thymeleaf HTML file name
        } else {
            return "error";
        }
    }

    @PostMapping("/submitPayment")
    public String submitPayment(
            @RequestParam("registrationNumber") String registrationNumber,
            @RequestParam("semester") String semester,
            @RequestParam("paymentType") String paymentType,
            @RequestParam("amount") double amount,
            @RequestParam("slipRefNumber") String slipRefNumber,
            @RequestParam("pdfImageFile") MultipartFile file,
            HttpSession session,
            Model model) throws Exception {

        String email = (String) session.getAttribute("loggedInUserEmail");
        if (email == null) {
            return "redirect:/login";
        }

        Optional<Student> studentOptional = studentRepository.findByEmailIgnoreCase(email);
        if (!studentOptional.isPresent()) {
            return "error";
        }

        Student student = studentOptional.get();

        // Upload file to Google Drive
        java.io.File convFile = new java.io.File("temp_" + file.getOriginalFilename());
        file.transferTo(convFile);
        String mimeType = file.getContentType();
        String fileUrl = googleDriveService.uploadFileToDrive(convFile, mimeType);
        convFile.delete(); // Delete temporary file

        // Create and save payment
        Payment payment = new Payment();
        payment.setDate(LocalDate.now().toString());
        payment.setAmount(amount);
        payment.setType(paymentType);
        payment.setSemester(semester);
        payment.setSlipRefNumber(slipRefNumber);
        payment.setStudent(student);
        payment.setFileUrl(fileUrl); // You'll need to add this field to your Payment entity

        paymentRepository.save(payment);

        return "redirect:/Student_Payments";
    }


    @Autowired
    private GoogleDriveService googleDriveService;

    @PostMapping("/student/uploadSlip")
    public String uploadSlip(@RequestParam("file") MultipartFile multipartFile,
                             @RequestParam("studentId") String studentId,
                             Model model) throws Exception {

        java.io.File convFile = new java.io.File("temp_" + multipartFile.getOriginalFilename());
        multipartFile.transferTo(convFile);

        String mimeType = multipartFile.getContentType();
        String fileUrl = googleDriveService.uploadFileToDrive(convFile, mimeType);

        // Save fileUrl to DB with studentId (you can use JPA or JDBC here)

        model.addAttribute("message", "Uploaded Successfully. URL: " + fileUrl);
        return "Student_Payments"; // or your appropriate view
    }













}
