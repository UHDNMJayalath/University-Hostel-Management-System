package com.hostelManagementSystem.HostelManagementSystem.service;

import org.springframework.stereotype.Service;

@Service
public class DeansService {

    public String getFacultyFromDeanEmail(String email) {
        if (email == null) {
            return "";
        }

        switch (email.toLowerCase()) {
            case " dean@pdn.ac.lk":
                return "Science";
            case "dean_arts@pdn.ac.lk":
                return "Arts";
            case "dean_mgt@pdn.ac.lk":
                return "Management";
            case "dean_agri@pdn.ac.lk":
                return "Agriculture";
            case "dean_med@pdn.ac.lk":
                return "Medicine";
            default:
                return "";
        }
    }
}
