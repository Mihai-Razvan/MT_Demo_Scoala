package com.example.demo_scoala.restcontrollers;

import com.example.demo_scoala.exceptions.NoFoundException;
import com.example.demo_scoala.models.Student;
import com.example.demo_scoala.services.StudentsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rest/students")
public class RestStudentsController {

    private final StudentsService studentsService;

    public RestStudentsController(StudentsService studentsService) {
        this.studentsService = studentsService;
    }

    @GetMapping("/show")
    public List<Student> showStudentByClass(@RequestParam String classCode) {
        try {
            return studentsService.getStudentsByClassCode(classCode);
        } catch (NoFoundException e) {
            return null;
        }
    }
    @PostMapping("/add")
    public Student addStudent(@RequestBody Map<String, String> body) {
        try {
            return studentsService.addStudentToClass(body);
        } catch (NoFoundException e) {
            return null;
        }
    }

    @PatchMapping("/move")
    public Student moveStudent(@RequestBody Map<String, String> body) {
        try {
            return studentsService.moveStudentToOtherClass(body);
        } catch (NoFoundException e) {
            return null;
        }
    }

    @DeleteMapping("/delete")
    public Student deleteStudent(@RequestBody Map<String, String> body) {
        try {
            return studentsService.deleteStudent(body);
        } catch (NoFoundException e) {
            return null;
        }
    }
}