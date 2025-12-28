package com.example.learningplatform.config;

import com.example.learningplatform.entity.AnswerOption;
import com.example.learningplatform.entity.Assignment;
import com.example.learningplatform.entity.Category;
import com.example.learningplatform.entity.Course;
import com.example.learningplatform.entity.Lesson;
import com.example.learningplatform.entity.Module;
import com.example.learningplatform.entity.Question;
import com.example.learningplatform.entity.QuestionType;
import com.example.learningplatform.entity.Quiz;
import com.example.learningplatform.entity.Tag;
import com.example.learningplatform.entity.User;
import com.example.learningplatform.entity.UserRole;
import com.example.learningplatform.repository.CategoryRepository;
import com.example.learningplatform.repository.CourseRepository;
import com.example.learningplatform.repository.ModuleRepository;
import com.example.learningplatform.repository.QuizRepository;
import com.example.learningplatform.repository.TagRepository;
import com.example.learningplatform.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@Profile("dev")
public class DemoDataLoader implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(DemoDataLoader.class);

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CourseRepository courseRepository;
    private final ModuleRepository moduleRepository;
    private final QuizRepository quizRepository;
    private final TagRepository tagRepository;

    public DemoDataLoader(UserRepository userRepository,
                          CategoryRepository categoryRepository,
                          CourseRepository courseRepository,
                          ModuleRepository moduleRepository,
                          QuizRepository quizRepository,
                          TagRepository tagRepository) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.courseRepository = courseRepository;
        this.moduleRepository = moduleRepository;
        this.quizRepository = quizRepository;
        this.tagRepository = tagRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        User teacher = getOrCreateUser("teacher@lp.local", "Teacher One", UserRole.TEACHER);
        User student1 = getOrCreateUser("student1@lp.local", "Student One", UserRole.STUDENT);
        User student2 = getOrCreateUser("student2@lp.local", "Student Two", UserRole.STUDENT);

        Category category = categoryRepository.findByNameIgnoreCase("ORM & Hibernate")
                .orElseGet(() -> {
                    Category created = new Category();
                    created.setName("ORM & Hibernate");
                    return categoryRepository.save(created);
                });

        Course course = courseRepository.findByTitleIgnoreCase("Hibernate Fundamentals")
                .orElseGet(() -> {
                    Course created = new Course();
                    created.setTitle("Hibernate Fundamentals");
                    created.setDescription("Hands-on course on ORM and Hibernate");
                    created.setDuration(20);
                    created.setStartDate(LocalDate.now().plusDays(7));
                    created.setCategory(category);
                    created.setTeacher(teacher);
                    return courseRepository.save(created);
                });

        Tag hibernateTag = getOrCreateTag("Hibernate");
        Tag jpaTag = getOrCreateTag("JPA");
        course.addTag(hibernateTag);
        course.addTag(jpaTag);
        courseRepository.save(course);

        if (moduleRepository.findByCourseIdOrderByOrderIndexAsc(course.getId()).isEmpty()) {
            Module module1 = new Module();
            module1.setTitle("ORM Basics");
            module1.setDescription("Entity mapping and persistence context");
            module1.setOrderIndex(1);
            course.addModule(module1);

            Lesson lesson1 = new Lesson();
            lesson1.setTitle("Entity Mapping");
            lesson1.setContent("Mapping entities and relationships");
            lesson1.setVideoUrl("https://example.com/video-entity-mapping");
            module1.addLesson(lesson1);

            Assignment assignment1 = new Assignment();
            assignment1.setTitle("Map your first entity");
            assignment1.setDescription("Create entity classes with JPA annotations");
            assignment1.setDueDate(LocalDate.now().plusDays(14));
            assignment1.setMaxScore(100);
            lesson1.addAssignment(assignment1);

            Module module2 = new Module();
            module2.setTitle("Hibernate Querying");
            module2.setDescription("JPQL and Criteria API");
            module2.setOrderIndex(2);
            course.addModule(module2);

            Lesson lesson2 = new Lesson();
            lesson2.setTitle("JPQL Essentials");
            lesson2.setContent("Write queries for entity graphs");
            lesson2.setVideoUrl("https://example.com/video-jpql");
            module2.addLesson(lesson2);

            Assignment assignment2 = new Assignment();
            assignment2.setTitle("Write JPQL queries");
            assignment2.setDescription("Practice joins and projections");
            assignment2.setDueDate(LocalDate.now().plusDays(21));
            assignment2.setMaxScore(100);
            lesson2.addAssignment(assignment2);

            courseRepository.save(course);
            log.info("Seeded modules, lessons, assignments for course id={}", course.getId());
        }

        List<Module> modules = moduleRepository.findByCourseIdOrderByOrderIndexAsc(course.getId());
        if (!modules.isEmpty()) {
            Module module1 = modules.get(0);
            if (module1.getQuiz() == null) {
                Quiz quiz = new Quiz();
                quiz.setTitle("ORM Basics Quiz");
                quiz.setTimeLimit(30);
                module1.setQuiz(quiz);

                Question question1 = new Question();
                question1.setText("What annotation marks an entity class?");
                question1.setType(QuestionType.SINGLE_CHOICE);
                AnswerOption q1o1 = new AnswerOption();
                q1o1.setText("@Entity");
                q1o1.setCorrect(true);
                AnswerOption q1o2 = new AnswerOption();
                q1o2.setText("@Table");
                q1o2.setCorrect(false);
                question1.addOption(q1o1);
                question1.addOption(q1o2);
                quiz.addQuestion(question1);

                Question question2 = new Question();
                question2.setText("Select valid JPA relationship annotations");
                question2.setType(QuestionType.MULTIPLE_CHOICE);
                AnswerOption q2o1 = new AnswerOption();
                q2o1.setText("@OneToMany");
                q2o1.setCorrect(true);
                AnswerOption q2o2 = new AnswerOption();
                q2o2.setText("@ManyToMany");
                q2o2.setCorrect(true);
                AnswerOption q2o3 = new AnswerOption();
                q2o3.setText("@Column");
                q2o3.setCorrect(false);
                question2.addOption(q2o1);
                question2.addOption(q2o2);
                question2.addOption(q2o3);
                quiz.addQuestion(question2);

                quizRepository.save(quiz);
                log.info("Seeded quiz for module id={}", module1.getId());
            }
        }

        log.info("Demo data ready: teacher={}, students={}, {}", teacher.getEmail(), List.of(student1.getEmail(), student2.getEmail()), course.getTitle());
    }

    private User getOrCreateUser(String email, String name, UserRole role) {
        return userRepository.findByEmail(email).orElseGet(() -> {
            User user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setRole(role);
            return userRepository.save(user);
        });
    }

    private Tag getOrCreateTag(String name) {
        return tagRepository.findByNameIgnoreCase(name).orElseGet(() -> {
            Tag tag = new Tag();
            tag.setName(name);
            return tagRepository.save(tag);
        });
    }
}
