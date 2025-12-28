package com.example.learningplatform.web.ui;

import com.example.learningplatform.dto.CourseResponse;
import com.example.learningplatform.dto.EnrollmentResponse;
import com.example.learningplatform.dto.StudentCourseView;
import com.example.learningplatform.dto.StudentQuizResultView;
import com.example.learningplatform.dto.StudentSubmissionView;
import com.example.learningplatform.dto.UserSummaryResponse;
import com.example.learningplatform.service.AssignmentService;
import com.example.learningplatform.service.CourseService;
import com.example.learningplatform.service.EnrollmentService;
import com.example.learningplatform.service.QuizService;
import com.example.learningplatform.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/ui/students")
public class UiStudentController {
    private final UserService userService;
    private final EnrollmentService enrollmentService;
    private final CourseService courseService;
    private final AssignmentService assignmentService;
    private final QuizService quizService;

    public UiStudentController(UserService userService,
                               EnrollmentService enrollmentService,
                               CourseService courseService,
                               AssignmentService assignmentService,
                               QuizService quizService) {
        this.userService = userService;
        this.enrollmentService = enrollmentService;
        this.courseService = courseService;
        this.assignmentService = assignmentService;
        this.quizService = quizService;
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public String studentDetails(@PathVariable Long id, Model model) {
        UserSummaryResponse student = userService.getStudent(id);

        List<EnrollmentResponse> enrollments = enrollmentService.listStudentCourses(id);
        List<StudentCourseView> courses = enrollments.stream()
                .map(enrollment -> {
                    CourseResponse course = courseService.getCourse(enrollment.courseId());
                    return new StudentCourseView(
                            enrollment.courseId(),
                            course.title(),
                            enrollment.status(),
                            enrollment.enrollDate()
                    );
                })
                .toList();

        List<StudentSubmissionView> submissions = assignmentService.listStudentSubmissions(id);
        List<StudentQuizResultView> quizResults = quizService.listStudentQuizResultsView(id);

        model.addAttribute("student", student);
        model.addAttribute("courses", courses);
        model.addAttribute("submissions", submissions);
        model.addAttribute("quizResults", quizResults);
        return "student-details";
    }
}
