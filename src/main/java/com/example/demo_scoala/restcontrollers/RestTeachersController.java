package com.example.demo_scoala.restcontrollers;

import com.example.demo_scoala.JsonTransform;
import com.example.demo_scoala.repositories.TeachersRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest/teachers")
public class RestTeachersController {

    private final JsonTransform jsonTransform;

    public RestTeachersController(JsonTransform jsonTransform) {
        this.jsonTransform = jsonTransform;
    }

    @GetMapping("/show")
    public String showTeachers() {

        try {
            return jsonTransform.teachersToJson();
        } catch (JsonProcessingException e) {
            return "ERROR";
        }
    }
}