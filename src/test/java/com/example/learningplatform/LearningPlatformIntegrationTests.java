package com.example.learningplatform;

import com.example.learningplatform.dto.CreateQuestionRequest;
import com.example.learningplatform.dto.CreateQuizRequest;
import com.example.learningplatform.dto.QuestionAnswerRequest;
import com.example.learningplatform.dto.SubmitAssignmentRequest;
import com.example.learningplatform.dto.TakeQuizRequest;
import com.example.learningplatform.entity.Assignment;
import com.example.learningplatform.entity.Category;
import com.example.learningplatform.entity.Course;
import com.example.learningplatform.entity.Lesson;
import com.example.learningplatform.entity.Module;
import com.example.learningplatform.entity.QuestionType;
import com.example.learningplatform.entity.User;
import com.example.learningplatform.entity.UserRole;
import com.example.learningplatform.exception.ConflictException;
import com.example.learningplatform.repository.AssignmentRepository;
import com.example.learningplatform.repository.CategoryRepository;
import com.example.learningplatform.repository.CourseRepository;
import com.example.learningplatform.repository.LessonRepository;
import com.example.learningplatform.repository.ModuleRepository;
import com.example.learningplatform.repository.QuizSubmissionRepository;
import com.example.learningplatform.repository.UserRepository;
import com.example.learningplatform.service.AssignmentService;
import com.example.learningplatform.service.EnrollmentService;
import com.example.learningplatform.service.QuizService;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
class LearningPlatformIntegrationTests {

    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void registerDataSource(DynamicPropertyRegistry registry) {
        if (!POSTGRES.isRunning()) {
            POSTGRES.start();
        }
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private QuizSubmissionRepository quizSubmissionRepository;

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private QuizService quizService;

    @Test
    void courseCascadePersist() {
        User teacher = createUser("Teacher", UserRole.TEACHER);
        Category category = createCategory("Cascade " + UUID.randomUUID());

        Course course = new Course();
        course.setTitle("Cascade Course " + UUID.randomUUID());
        course.setDescription("Cascade test");
        course.setDuration(10);
        course.setStartDate(LocalDate.now());
        course.setCategory(category);
        course.setTeacher(teacher);

        Module module = new Module();
        module.setTitle("Module 1");
        module.setDescription("Module desc");
        module.setOrderIndex(1);
        course.addModule(module);

        Lesson lesson = new Lesson();
        lesson.setTitle("Lesson 1");
        lesson.setContent("Content");
        lesson.setVideoUrl("https://example.com/video");
        module.addLesson(lesson);

        Assignment assignment = new Assignment();
        assignment.setTitle("Assignment 1");
        assignment.setDescription("Description");
        assignment.setDueDate(LocalDate.now().plusDays(7));
        assignment.setMaxScore(100);
        lesson.addAssignment(assignment);

        Course saved = courseRepository.save(course);
        assertThat(saved.getId()).isNotNull();
        assertThat(module.getId()).isNotNull();
        assertThat(lesson.getId()).isNotNull();
        assertThat(assignment.getId()).isNotNull();

        Course fetched = courseRepository.findWithStructureById(saved.getId()).orElseThrow();
        assertThat(fetched.getModules()).hasSize(1);
        assertThat(fetched.getModules().get(0).getLessons()).hasSize(1);
        assertThat(fetched.getModules().get(0).getLessons().get(0).getAssignments()).hasSize(1);
    }

    @Test
    void enrollmentUniqueness() {
        User teacher = createUser("Enroll Teacher", UserRole.TEACHER);
        User student = createUser("Enroll Student", UserRole.STUDENT);
        Category category = createCategory("Enroll " + UUID.randomUUID());

        Course course = new Course();
        course.setTitle("Enroll Course " + UUID.randomUUID());
        course.setDescription("Enroll test");
        course.setDuration(8);
        course.setStartDate(LocalDate.now());
        course.setCategory(category);
        course.setTeacher(teacher);
        courseRepository.save(course);

        enrollmentService.enrollStudent(course.getId(), student.getId());
        assertThatThrownBy(() -> enrollmentService.enrollStudent(course.getId(), student.getId()))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void submissionUniqueness() {
        User teacher = createUser("Submit Teacher", UserRole.TEACHER);
        User student = createUser("Submit Student", UserRole.STUDENT);
        Category category = createCategory("Submit " + UUID.randomUUID());

        Course course = new Course();
        course.setTitle("Submit Course " + UUID.randomUUID());
        course.setDescription("Submit test");
        course.setDuration(6);
        course.setStartDate(LocalDate.now());
        course.setCategory(category);
        course.setTeacher(teacher);
        courseRepository.save(course);

        Module module = new Module();
        module.setTitle("Module Submit");
        module.setDescription("Desc");
        module.setOrderIndex(1);
        course.addModule(module);
        moduleRepository.save(module);

        Lesson lesson = new Lesson();
        lesson.setTitle("Lesson Submit");
        lesson.setContent("Content");
        lesson.setVideoUrl("https://example.com/video");
        module.addLesson(lesson);
        lessonRepository.save(lesson);

        Assignment assignment = new Assignment();
        assignment.setTitle("Assignment Submit");
        assignment.setDescription("Desc");
        assignment.setDueDate(LocalDate.now().plusDays(3));
        assignment.setMaxScore(100);
        lesson.addAssignment(assignment);
        assignmentRepository.save(assignment);

        SubmitAssignmentRequest request = new SubmitAssignmentRequest(student.getId(), "Answer");
        assignmentService.submitAssignment(assignment.getId(), student.getId(), request);
        assertThatThrownBy(() -> assignmentService.submitAssignment(assignment.getId(), student.getId(), request))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void quizTakeScoresAndPersists() {
        User teacher = createUser("Quiz Teacher", UserRole.TEACHER);
        User student = createUser("Quiz Student", UserRole.STUDENT);
        Category category = createCategory("Quiz " + UUID.randomUUID());

        Course course = new Course();
        course.setTitle("Quiz Course " + UUID.randomUUID());
        course.setDescription("Quiz test");
        course.setDuration(12);
        course.setStartDate(LocalDate.now());
        course.setCategory(category);
        course.setTeacher(teacher);
        courseRepository.save(course);

        Module module = new Module();
        module.setTitle("Quiz Module");
        module.setDescription("Desc");
        module.setOrderIndex(1);
        course.addModule(module);
        moduleRepository.save(module);

        var quizResponse = quizService.createQuizForModule(module.getId(), new CreateQuizRequest("Quiz 1", 15));

        CreateQuestionRequest q1 = new CreateQuestionRequest(
                "What annotation marks entity?",
                QuestionType.SINGLE_CHOICE,
                List.of(
                        new com.example.learningplatform.dto.CreateAnswerOptionRequest("@Entity", true),
                        new com.example.learningplatform.dto.CreateAnswerOptionRequest("@Table", false)
                )
        );
        var question1 = quizService.addQuestion(quizResponse.id(), q1);

        CreateQuestionRequest q2 = new CreateQuestionRequest(
                "Select relationship annotations",
                QuestionType.MULTIPLE_CHOICE,
                List.of(
                        new com.example.learningplatform.dto.CreateAnswerOptionRequest("@OneToMany", true),
                        new com.example.learningplatform.dto.CreateAnswerOptionRequest("@ManyToMany", true),
                        new com.example.learningplatform.dto.CreateAnswerOptionRequest("@Column", false)
                )
        );
        var question2 = quizService.addQuestion(quizResponse.id(), q2);

        Set<Long> q1Correct = question1.options().stream()
                .filter(com.example.learningplatform.dto.AnswerOptionResponse::isCorrect)
                .map(com.example.learningplatform.dto.AnswerOptionResponse::id)
                .collect(java.util.stream.Collectors.toSet());
        Set<Long> q2Correct = question2.options().stream()
                .filter(com.example.learningplatform.dto.AnswerOptionResponse::isCorrect)
                .map(com.example.learningplatform.dto.AnswerOptionResponse::id)
                .collect(java.util.stream.Collectors.toSet());

        TakeQuizRequest request = new TakeQuizRequest(
                student.getId(),
                List.of(
                        new QuestionAnswerRequest(question1.id(), List.copyOf(q1Correct)),
                        new QuestionAnswerRequest(question2.id(), List.copyOf(q2Correct))
                )
        );

        var result = quizService.takeQuiz(quizResponse.id(), student.getId(), request);
        assertThat(result.score()).isEqualTo(2);
        assertThat(quizSubmissionRepository.findByStudentId(student.getId())).isNotEmpty();
    }

    @Test
    void lazyInitializationExceptionOccurs() {
        User teacher = createUser("Lazy Teacher", UserRole.TEACHER);
        Category category = createCategory("Lazy " + UUID.randomUUID());

        Course course = new Course();
        course.setTitle("Lazy Course " + UUID.randomUUID());
        course.setDescription("Lazy test");
        course.setDuration(5);
        course.setStartDate(LocalDate.now());
        course.setCategory(category);
        course.setTeacher(teacher);

        Module module = new Module();
        module.setTitle("Lazy Module");
        module.setDescription("Desc");
        module.setOrderIndex(1);
        course.addModule(module);
        courseRepository.save(course);

        Course loaded = courseRepository.findById(course.getId()).orElseThrow();
        assertThatThrownBy(() -> loaded.getModules().size())
                .isInstanceOf(LazyInitializationException.class);
    }

    @Test
    void entityGraphPreventsLazyInitializationException() {
        User teacher = createUser("Graph Teacher", UserRole.TEACHER);
        Category category = createCategory("Graph " + UUID.randomUUID());

        Course course = new Course();
        course.setTitle("Graph Course " + UUID.randomUUID());
        course.setDescription("Graph test");
        course.setDuration(7);
        course.setStartDate(LocalDate.now());
        course.setCategory(category);
        course.setTeacher(teacher);

        Module module = new Module();
        module.setTitle("Graph Module");
        module.setDescription("Desc");
        module.setOrderIndex(1);
        course.addModule(module);
        courseRepository.save(course);

        Course loaded = courseRepository.findWithStructureById(course.getId()).orElseThrow();
        assertThat(loaded.getModules()).hasSize(1);
    }

    private User createUser(String baseName, UserRole role) {
        User user = new User();
        user.setName(baseName);
        user.setEmail(baseName.replace(" ", "").toLowerCase() + "-" + UUID.randomUUID() + "@test.local");
        user.setRole(role);
        return userRepository.save(user);
    }

    private Category createCategory(String name) {
        Category category = new Category();
        category.setName(name);
        return categoryRepository.save(category);
    }
}
