package com.example.demo_scoala.restcontrollers;

import com.example.demo_scoala.JsonTransform;
import com.example.demo_scoala.models.Class;
import com.example.demo_scoala.models.Student;
import com.example.demo_scoala.repositories.ClassesRepository;
import com.example.demo_scoala.repositories.StudentsRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.boot.json.JsonParser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/rest/students")
public class RestStudentsController {

    private final StudentsRepository studentsRepository;
    private final ClassesRepository classesRepository;
    private final JsonTransform jsonTransform;

    public RestStudentsController(StudentsRepository studentsRepository, ClassesRepository classesRepository, JsonTransform jsonTransform) {
        this.studentsRepository = studentsRepository;
        this.classesRepository = classesRepository;
        this.jsonTransform = jsonTransform;
    }

    @GetMapping("/show")
    public String showStudentByClass(@RequestParam String classCode) {

        try {
            return jsonTransform.studentsToJson(classCode);
        } catch (JsonProcessingException e) {
            return "ERROR";
        }
    }

    @PostMapping("/add")
    public String addStudent(@RequestBody Map<String, String> body) {  //adds a student to the given class (by code) and returns all the students in that class

        Optional<Class> clasa = classesRepository.findByCode(body.get("classCode"));

        if(clasa.isPresent()) {
            Student newStudent = new Student(body.get("firstName"), body.get("lastName"), Integer.parseInt(body.get("age")), clasa.get());
            studentsRepository.save(newStudent);

            try {
                return jsonTransform.studentsToJson(body.get("classCode"));
            } catch (JsonProcessingException e) {
                return "ERROR";
            }
        }
        else
            return "ERROR";
    }

    @PatchMapping("/move")
    public String moveStudent(@RequestBody Map<String, String> body) {   //move a student from a class to the given new class and returns all the students in that new class

        Optional<Student> student = studentsRepository.findByFirstNameAndLastName(body.get("firstName"), body.get("lastName"));
        Optional<Class> newClass = classesRepository.findByCode(body.get("newClassCode"));

        if(student.isPresent() && newClass.isPresent()) {
            Student updatedStudent = student.get();
            updatedStudent.setClasa(newClass.get());
            studentsRepository.save(updatedStudent);

            try {
                return jsonTransform.studentsToJson(body.get("newClassCode"));
            } catch (JsonProcessingException e) {
                return "ERROR";
            }
        }
        else
            return "ERROR";
    }

    @DeleteMapping("/delete")
    public String deleteStudent(@RequestBody Map<String, String> body) {  //given the firstName and lastName, deletes that student and returns all students in his former class

        Optional<Student> student = studentsRepository.findByFirstNameAndLastName(body.get("firstName"), body.get("lastName"));

        if(student.isPresent()) {
            String classCode = student.get().getClasa().getCode();
            studentsRepository.delete(student.get());

            try {
                return jsonTransform.studentsToJson(classCode);
            } catch (JsonProcessingException e) {
                return "ERROR";
            }
        }
        else
            return "ERROR";
    }
}