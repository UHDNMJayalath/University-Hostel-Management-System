package com.hostelManagementSystem.HostelManagementSystem.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Controller
public class ExcelUploadController {

    private static final String UPLOAD_DIR = "uploads/excelSheets";

    @PostMapping("/upload-excel")
    public String handleExcelUpload(@RequestParam("excelFile")MultipartFile excelSheet,
                                    @RequestParam("faculty") String faculty,
                                    @RequestParam("intake") String intake,
                                    HttpServletRequest request,
                                    Model model){

        CsrfToken csrf = (CsrfToken) request.getAttribute("_csrf");
        model.addAttribute("_csrf", csrf);
        System.out.println("First part");

        try{
            // Create upload directory if not exists
            Files.createDirectories(Paths.get(UPLOAD_DIR));

            // Sanitize inputs
            String safeFaculty = faculty.replaceAll("[^a-zA-Z0-9]","_");
            String safeIntake = intake.replaceAll("[^a-zA-Z0-9]","_");
            System.out.println("Renamed Edited");

            // Get original extension
            String originalFileName = StringUtils.cleanPath(Objects.requireNonNull(excelSheet.getOriginalFilename()));
            String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            System.out.println("Got original extension");

            // Create new file name
            String newFileName = safeFaculty + "_" + safeIntake + extension;
            System.out.println("Renamed successfully");

            // Save the file
            Path destination = Paths.get(UPLOAD_DIR).resolve(newFileName);
            Files.copy(excelSheet.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Saved the file");

            model.addAttribute("message", "File uploaded successfully: " + newFileName);
            return "redirect:/SRB_history"; // Redirect or show success message page

        }
        catch (Exception e){
            e.printStackTrace();
            model.addAttribute("error", "Failed to upload file.");
            return "redirect:/SRB_history"; // Error page or message
        }
    }

    @RequestMapping("/SRB_history")
    public String getHistory(){
        return "SRB_history";
    }
}
