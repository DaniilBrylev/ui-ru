package com.example.learningplatform.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    private Integer duration;

    private LocalDate startDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private User teacher;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderColumn(name = "module_order")
    private List<Module> modules = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Enrollment> enrollments = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CourseReview> reviews = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "course_tag",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"),
            uniqueConstraints = @UniqueConstraint(name = "uk_course_tag", columnNames = {"course_id", "tag_id"})
    )
    private Set<Tag> tags = new HashSet<>();

    public void addModule(Module module) {
        if (module == null) {
            return;
        }
        modules.add(module);
        module.setCourse(this);
    }

    public void removeModule(Module module) {
        if (module == null) {
            return;
        }
        modules.remove(module);
        if (module.getCourse() == this) {
            module.setCourse(null);
        }
    }

    public void addEnrollment(Enrollment enrollment) {
        if (enrollment == null) {
            return;
        }
        enrollments.add(enrollment);
        enrollment.setCourse(this);
    }

    public void removeEnrollment(Enrollment enrollment) {
        if (enrollment == null) {
            return;
        }
        enrollments.remove(enrollment);
        if (enrollment.getCourse() == this) {
            enrollment.setCourse(null);
        }
    }

    public void addReview(CourseReview review) {
        if (review == null) {
            return;
        }
        reviews.add(review);
        review.setCourse(this);
    }

    public void removeReview(CourseReview review) {
        if (review == null) {
            return;
        }
        reviews.remove(review);
        if (review.getCourse() == this) {
            review.setCourse(null);
        }
    }

    public void addTag(Tag tag) {
        if (tag == null) {
            return;
        }
        tags.add(tag);
        tag.getCourses().add(this);
    }

    public void removeTag(Tag tag) {
        if (tag == null) {
            return;
        }
        tags.remove(tag);
        tag.getCourses().remove(this);
    }
}
