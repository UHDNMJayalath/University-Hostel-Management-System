package com.hostelManagementSystem.HostelManagementSystem.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "hostel")
public class Hostel {

    @Id
    private Long id;    // primary key

    private String name;
    private String location;
    private String faculty;  // Added faculty field

    // Constructors
    public Hostel() {
    }

    public Hostel(Long id, String name, String location, String faculty) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.faculty = faculty;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    // Optional: toString() method
    @Override
    public String toString() {
        return "Hostel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", faculty='" + faculty + '\'' +
                '}';
    }
}