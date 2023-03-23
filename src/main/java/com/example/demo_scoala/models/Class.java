package com.example.demo_scoala.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.persistence.*;

import java.util.List;

@Entity(name = "Class")
public class Class {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String profile;
    private Integer number;           //clasa 1, 2, 3...12
    private String code;
    @JsonIgnore
    @OneToMany(mappedBy = "clasa")
    private List<Student> students;
    @JsonIgnore
    @ManyToMany(mappedBy = "classes")
    private List<Teacher> teachers;

    public Class() {}

    public Class(String profile, Integer number, String code) {
        this.profile = profile;
        this.number = number;
        this.code = code;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public List<Teacher> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<Teacher> teachers) {
        this.teachers = teachers;
    }

    @Override
    public String toString() {
        return "{" +
                "\"profile\":" + "\"" + profile + "\"" +
                ", \"number\":" + number +
                ", \"code\":" + "\"" + code + "\"" +
                "}";
    }
}
