package com.example.demo_scoala.restcontrollers;

import com.example.demo_scoala.services.TeachersService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest/teachers")
public class RestTeachersController {

    private final TeachersService teachersService;

    public RestTeachersController(TeachersService teachersService) {
        this.teachersService = teachersService;
    }

    @GetMapping("/show")
    public String showTeachers() {
        return teachersService.getTeachers();
    }
}