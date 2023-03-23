package com.example.demo_scoala.controllers;

import com.example.demo_scoala.repositories.ClassesRepository;
import com.example.demo_scoala.repositories.TeachersRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/teachers")
public class TeachersController {

    private final TeachersRepository teachersRepository;

    public TeachersController(TeachersRepository teachersRepository) {
        this.teachersRepository = teachersRepository;
    }

    @GetMapping("/show")
    public String showTeachers(Model model) {

        model.addAttribute("teachers", teachersRepository.findAll());
        return "teachers/show";
    }

    @GetMapping("test")    //some testing
    @ResponseBody
    public String test(Model model) {

        return "TEEEESST";
    }
}
