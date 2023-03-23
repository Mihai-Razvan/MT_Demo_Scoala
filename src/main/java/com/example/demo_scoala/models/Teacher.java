package com.example.demo_scoala.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.persistence.*;

import java.util.List;

@Entity(name = "Teacher")
public class Teacher extends Person{

    private String subject;
    @ManyToMany
    @JoinTable(name = "teacher_class", joinColumns = {@JoinColumn(name = "teacher_id")}, inverseJoinColumns = {@JoinColumn(name = "class_id")})
    private List<Class> classes;

    public Teacher() {}

    public Teacher(String firstName, String lastName, String subject, List<Class> classes) {
        super(firstName, lastName);
        this.subject = subject;
        this.classes = classes;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public List<Class> getClasses() {
        return classes;
    }

    public void setClasses(List<Class> classes) {
        this.classes = classes;
    }

    @Override
    public String toString() {
        return "{" +
                "\"firstName\":" + "\"" + getFirstName() + "\"" +
                ", \"lastName\":" + "\"" + getLastName() + "\"" +
                ", \"subject\":" + "\"" + subject + "\"" +
                ", \"classes\":" + classes +
                "}";
    }
}
