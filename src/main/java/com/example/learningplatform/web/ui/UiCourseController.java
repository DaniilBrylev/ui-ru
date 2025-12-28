package com.example.learningplatform.web.ui;

import com.example.learningplatform.dto.CourseResponse;
import com.example.learningplatform.dto.CourseStructureResponse;
import com.example.learningplatform.dto.EnrollmentResponse;
import com.example.learningplatform.dto.UserSummaryResponse;
import com.example.learningplatform.service.CourseService;
import com.example.learningplatform.service.EnrollmentService;
import com.example.learningplatform.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/ui/courses")
public class UiCourseController {
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final UserService userService;

    public UiCourseController(CourseService courseService,
                              EnrollmentService enrollmentService,
                              UserService userService) {
        this.courseService = courseService;
        this.enrollmentService = enrollmentService;
        this.userService = userService;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public String listCourses(Model model) {
        List<CourseResponse> courses = courseService.getAllCourses();
        model.addAttribute("courses", courses);
        return "courses";
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public String courseDetails(@PathVariable Long id, Model model) {
        CourseStructureResponse course = courseService.getCourseStructure(id);
        List<UserSummaryResponse> students = userService.listStudents();
        List<EnrollmentResponse> enrollments = enrollmentService.listCourseStudents(id);

        Map<Long, UserSummaryResponse> studentsById = students.stream()
                .collect(Collectors.toMap(UserSummaryResponse::id, student -> student));

        model.addAttribute("course", course);
        model.addAttribute("students", students);
        model.addAttribute("enrollments", enrollments);
        model.addAttribute("studentsById", studentsById);
        return "course-details";
    }

    @PostMapping("/{id}/enroll")
    public String enrollStudent(@PathVariable Long id,
                                @RequestParam Long studentId,
                                RedirectAttributes redirectAttributes) {
        enrollmentService.enrollStudent(id, studentId);
        redirectAttributes.addFlashAttribute("message", "Student enrolled successfully");
        return "redirect:/ui/courses/" + id;
    }
}
