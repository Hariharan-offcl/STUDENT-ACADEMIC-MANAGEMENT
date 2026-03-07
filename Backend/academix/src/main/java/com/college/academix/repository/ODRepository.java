package com.college.academix.repository;

import com.college.academix.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ODRepository extends JpaRepository<ODRequest, Long> {
    List<ODRequest> findByStudent(Student student);

    List<ODRequest> findBySection(String section);

    List<ODRequest> findByRollNumber(String rollNumber);

    List<ODRequest> findByStatus(ODStatus status);

    List<ODRequest> findBySectionAndStatus(String section, ODStatus status);
}