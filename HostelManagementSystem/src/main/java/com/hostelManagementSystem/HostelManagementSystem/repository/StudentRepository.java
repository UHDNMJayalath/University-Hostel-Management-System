package com.hostelManagementSystem.HostelManagementSystem.repository;

import com.hostelManagementSystem.HostelManagementSystem.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, String> {
    Optional<Student> findByEmailIgnoreCase(String email);

    Optional<Student> findByStudentId(String studentId);

    List<Student> findByFaculty(String faculty);
    Optional<Student> findByStudentIdAndFaculty(String studentId, String faculty);



}

