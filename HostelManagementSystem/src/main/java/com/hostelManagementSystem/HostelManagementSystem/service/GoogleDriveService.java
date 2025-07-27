package com.hostelManagementSystem.HostelManagementSystem.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
public class GoogleDriveService {

    private static final String APPLICATION_NAME = "HostelManagementSystem";
    private static final String SERVICE_ACCOUNT_KEY_PATH = "hms-project.json";

    private Drive getDriveService() throws IOException, GeneralSecurityException {
        GoogleCredential credential = GoogleCredential
                .fromStream(new FileInputStream(SERVICE_ACCOUNT_KEY_PATH))
                .createScoped(Collections.singleton("https://www.googleapis.com/auth/drive"));

        return new Drive.Builder(
                com.google.api.client.googleapis.javanet.GoogleNetHttpTransport.newTrustedTransport(),
                com.google.api.client.json.jackson2.JacksonFactory.getDefaultInstance(),
                credential
        ).setApplicationName(APPLICATION_NAME).build();
    }

    public String uploadFileToDrive(java.io.File file, String mimeType) throws Exception {
        Drive driveService = getDriveService();

        File fileMetadata = new File();
        fileMetadata.setName(file.getName());

        FileContent mediaContent = new FileContent(mimeType, file);
        File uploadedFile = driveService.files().create(fileMetadata, mediaContent)
                .setFields("id")
                .execute();

        // Make the file public
        driveService.permissions().create(uploadedFile.getId(),
                        new com.google.api.services.drive.model.Permission()
                                .setType("anyone")
                                .setRole("reader"))
                .execute();

        return "https://drive.google.com/uc?id=" + uploadedFile.getId();
    }
}
