package com.college.academix.repository;

import com.college.academix.model.Department;
import com.college.academix.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {
    List<Section> findByDepartment(Department department);

    Optional<Section> findByNameAndDepartment(String name, Department department);
}
