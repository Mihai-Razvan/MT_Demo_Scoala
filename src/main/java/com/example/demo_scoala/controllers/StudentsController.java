package com.example.demo_scoala.controllers;

import com.example.demo_scoala.models.Class;
import com.example.demo_scoala.models.Student;
import com.example.demo_scoala.repositories.ClassesRepository;
import com.example.demo_scoala.repositories.StudentsRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/students")
public class StudentsController {

    private final StudentsRepository studentsRepository;
    private final ClassesRepository classesRepository;

    public StudentsController(StudentsRepository studentsRepository, ClassesRepository classesRepository) {
        this.studentsRepository = studentsRepository;
        this.classesRepository = classesRepository;
    }

    @GetMapping("/show")
    public String showStudentByClass(@RequestParam String classCode, Model model) {

        model.addAttribute("students", studentsRepository.findByClasaCode(classCode));
        return "students/showByClass";
    }

    @PostMapping("/add")
    public String addStudent(@RequestBody Map<String, String> body, Model model) {

        Optional<Class> clasa = classesRepository.findByCode(body.get("classCode"));

        if(clasa.isPresent()) {
            Student newStudent = new Student(body.get("firstName"), body.get("lastName"), Integer.parseInt(body.get("age")), clasa.get());
            studentsRepository.save(newStudent);
            model.addAttribute("students", studentsRepository.findByClasaCode(body.get("classCode")));
            return "students/showByClass";
        }
        else
            return "wrongData";
    }

    @PatchMapping("/move")
    public String moveStudent(@RequestBody Map<String, String> body, Model model) {

        Optional<Student> student = studentsRepository.findByFirstNameAndLastName(body.get("firstName"), body.get("lastName"));
        Optional<Class> newClass = classesRepository.findByCode(body.get("newClassCode"));

        if(student.isPresent() && newClass.isPresent()) {
            Student updatedStudent = student.get();
            updatedStudent.setClasa(newClass.get());
            studentsRepository.save(updatedStudent);
            model.addAttribute("students", studentsRepository.findByClasaCode(body.get("newClassCode")));
            return "students/showByClass";
        }
        else
            return "wrongData";
    }

    @DeleteMapping("/delete")
    public String deleteStudent(@RequestBody Map<String, String> body, Model model) {

        Optional<Student> student = studentsRepository.findByFirstNameAndLastName(body.get("firstName"), body.get("lastName"));

        if(student.isPresent()) {
            String classCode = student.get().getClasa().getCode();
            studentsRepository.delete(student.get());
            model.addAttribute("students", studentsRepository.findByClasaCode(classCode));
            return "students/showByClass";
        }
        else
            return "wrongData";
    }
}
