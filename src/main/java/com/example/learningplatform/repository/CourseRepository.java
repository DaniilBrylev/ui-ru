package com.example.learningplatform.repository;

import com.example.learningplatform.entity.Course;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByTitleIgnoreCase(String title);

    @EntityGraph(attributePaths = {"modules", "modules.lessons", "modules.lessons.assignments"})
    Optional<Course> findWithStructureById(Long id);

    @Query("select distinct c from Course c " +
            "left join fetch c.modules m " +
            "left join fetch m.lessons l " +
            "left join fetch l.assignments " +
            "where c.id = :id")
    Optional<Course> findWithStructureByIdJoinFetch(@Param("id") Long id);
}
