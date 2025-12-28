package com.example.learningplatform.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(name = "uk_users_email", columnNames = "email")
})
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Profile profile;

    @OneToMany(mappedBy = "teacher", fetch = FetchType.LAZY)
    private List<Course> taughtCourses = new ArrayList<>();

    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
    private List<Enrollment> enrollments = new ArrayList<>();

    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
    private List<Submission> submissions = new ArrayList<>();

    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
    private List<QuizSubmission> quizSubmissions = new ArrayList<>();

    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
    private List<CourseReview> reviews = new ArrayList<>();

    public void setProfile(Profile profile) {
        if (this.profile != null) {
            this.profile.setUser(null);
        }
        this.profile = profile;
        if (profile != null && profile.getUser() != this) {
            profile.setUser(this);
        }
    }

    public void addEnrollment(Enrollment enrollment) {
        if (enrollment == null) {
            return;
        }
        enrollments.add(enrollment);
        enrollment.setStudent(this);
    }

    public void removeEnrollment(Enrollment enrollment) {
        if (enrollment == null) {
            return;
        }
        enrollments.remove(enrollment);
        enrollment.setStudent(null);
    }

    public void addSubmission(Submission submission) {
        if (submission == null) {
            return;
        }
        submissions.add(submission);
        submission.setStudent(this);
    }

    public void removeSubmission(Submission submission) {
        if (submission == null) {
            return;
        }
        submissions.remove(submission);
        submission.setStudent(null);
    }

    public void addQuizSubmission(QuizSubmission submission) {
        if (submission == null) {
            return;
        }
        quizSubmissions.add(submission);
        submission.setStudent(this);
    }

    public void removeQuizSubmission(QuizSubmission submission) {
        if (submission == null) {
            return;
        }
        quizSubmissions.remove(submission);
        submission.setStudent(null);
    }

    public void addReview(CourseReview review) {
        if (review == null) {
            return;
        }
        reviews.add(review);
        review.setStudent(this);
    }

    public void removeReview(CourseReview review) {
        if (review == null) {
            return;
        }
        reviews.remove(review);
        review.setStudent(null);
    }

    public void addTaughtCourse(Course course) {
        if (course == null) {
            return;
        }
        taughtCourses.add(course);
        course.setTeacher(this);
    }

    public void removeTaughtCourse(Course course) {
        if (course == null) {
            return;
        }
        taughtCourses.remove(course);
        if (course.getTeacher() == this) {
            course.setTeacher(null);
        }
    }
}
