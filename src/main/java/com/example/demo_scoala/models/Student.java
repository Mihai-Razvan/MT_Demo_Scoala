package com.example.demo_scoala.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.persistence.*;

@Entity(name = "Student")
public class Student extends Person{

    private Integer age;
    @ManyToOne
    @JoinColumn(name = "class_id")
    private Class clasa;

    public Student() {}

    public Student(String firstName, String lastName, Integer age, Class clasa) {
        super(firstName, lastName);
        this.age = age;
        this.clasa = clasa;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Class getClasa() {
        return clasa;
    }

    public void setClasa(Class clasa) {
        this.clasa = clasa;
    }

    @Override
    public String toString() {
        return "{" +
                "\"firstName\":" + "\"" + getFirstName() + "\"" +
                ", \"lastName\":" + "\"" + getLastName() + "\"" +
                ", \"age\":" + age +
                ", \"clasa\":" + clasa +
                "}";
    }
}
