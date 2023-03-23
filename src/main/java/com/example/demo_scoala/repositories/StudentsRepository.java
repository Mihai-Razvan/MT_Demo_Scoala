package com.example.demo_scoala.repositories;

import com.example.demo_scoala.models.Student;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentsRepository extends CrudRepository<Student, Long> {

    List<Student> findByClasaCode(String classCode);
    Optional<Student> findByFirstNameAndLastName(String firstName, String lastName);
}
