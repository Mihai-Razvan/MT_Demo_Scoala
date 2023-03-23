package com.example.demo_scoala.repositories;

import com.example.demo_scoala.models.Teacher;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeachersRepository extends CrudRepository<Teacher, Long> {
}
