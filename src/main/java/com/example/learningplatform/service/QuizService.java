package com.example.learningplatform.service;

import com.example.learningplatform.dto.AnswerOptionResponse;
import com.example.learningplatform.dto.CreateQuestionRequest;
import com.example.learningplatform.dto.CreateQuizRequest;
import com.example.learningplatform.dto.QuestionAnswerRequest;
import com.example.learningplatform.dto.QuestionResponse;
import com.example.learningplatform.dto.QuizResponse;
import com.example.learningplatform.dto.QuizSubmissionResponse;
import com.example.learningplatform.dto.StudentQuizResultView;
import com.example.learningplatform.dto.TakeQuizRequest;
import com.example.learningplatform.entity.AnswerOption;
import com.example.learningplatform.entity.Module;
import com.example.learningplatform.entity.Question;
import com.example.learningplatform.entity.QuestionType;
import com.example.learningplatform.entity.Quiz;
import com.example.learningplatform.entity.QuizSubmission;
import com.example.learningplatform.entity.User;
import com.example.learningplatform.entity.UserRole;
import com.example.learningplatform.exception.BadRequestException;
import com.example.learningplatform.exception.ConflictException;
import com.example.learningplatform.exception.NotFoundException;
import com.example.learningplatform.repository.ModuleRepository;
import com.example.learningplatform.repository.QuestionRepository;
import com.example.learningplatform.repository.QuizRepository;
import com.example.learningplatform.repository.QuizSubmissionRepository;
import com.example.learningplatform.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class QuizService {
    private static final Logger log = LoggerFactory.getLogger(QuizService.class);

    private final QuizRepository quizRepository;
    private final ModuleRepository moduleRepository;
    private final QuestionRepository questionRepository;
    private final QuizSubmissionRepository quizSubmissionRepository;
    private final UserRepository userRepository;

    public QuizService(QuizRepository quizRepository,
                       ModuleRepository moduleRepository,
                       QuestionRepository questionRepository,
                       QuizSubmissionRepository quizSubmissionRepository,
                       UserRepository userRepository) {
        this.quizRepository = quizRepository;
        this.moduleRepository = moduleRepository;
        this.questionRepository = questionRepository;
        this.quizSubmissionRepository = quizSubmissionRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public QuizResponse createQuizForModule(Long moduleId, CreateQuizRequest request) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new NotFoundException("Module not found: " + moduleId));
        if (module.getQuiz() != null) {
            throw new ConflictException("Quiz already exists for module");
        }

        Quiz quiz = new Quiz();
        quiz.setTitle(request.title());
        quiz.setTimeLimit(request.timeLimit());
        module.setQuiz(quiz);

        Quiz saved = quizRepository.save(quiz);
        log.info("Created quiz id={} for module id={}", saved.getId(), moduleId);
        return toQuizResponse(saved);
    }

    @Transactional
    public QuestionResponse addQuestion(Long quizId, CreateQuestionRequest request) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new NotFoundException("Quiz not found: " + quizId));

        Question question = new Question();
        question.setQuiz(quiz);
        question.setText(request.text());
        question.setType(request.type());

        request.options().forEach(optionRequest -> {
            AnswerOption option = new AnswerOption();
            option.setText(optionRequest.text());
            option.setCorrect(optionRequest.isCorrect());
            question.addOption(option);
        });

        quiz.addQuestion(question);
        Question savedQuestion = questionRepository.saveAndFlush(question);

        log.info("Added question id={} to quiz id={}", savedQuestion.getId(), quizId);
        return toQuestionResponse(savedQuestion);
    }

    @Transactional
    public QuizSubmissionResponse takeQuiz(Long quizId, Long studentId, TakeQuizRequest request) {
        Quiz quiz = quizRepository.findWithQuestionsById(quizId)
                .orElseThrow(() -> new NotFoundException("Quiz not found: " + quizId));
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found: " + studentId));
        if (student.getRole() != UserRole.STUDENT) {
            throw new BadRequestException("User is not a student");
        }

        Map<Long, Question> questions = new HashMap<>();
        for (Question question : quiz.getQuestions()) {
            questions.put(question.getId(), question);
        }

        int score = 0;
        for (QuestionAnswerRequest answer : request.answers()) {
            Question question = questions.get(answer.questionId());
            if (question == null) {
                throw new BadRequestException("Question does not belong to quiz: " + answer.questionId());
            }

            Set<Long> selected = new HashSet<>(answer.selectedOptionIds());
            Set<Long> optionIds = question.getOptions().stream()
                    .map(AnswerOption::getId)
                    .collect(Collectors.toSet());
            if (!optionIds.containsAll(selected)) {
                throw new BadRequestException("Selected option does not belong to question: " + question.getId());
            }

            Set<Long> correct = question.getOptions().stream()
                    .filter(AnswerOption::isCorrect)
                    .map(AnswerOption::getId)
                    .collect(Collectors.toSet());

            if (question.getType() == QuestionType.SINGLE_CHOICE && selected.size() != 1) {
                continue;
            }

            if (selected.equals(correct)) {
                score++;
            }
        }

        QuizSubmission submission = new QuizSubmission();
        submission.setQuiz(quiz);
        submission.setStudent(student);
        submission.setScore(score);
        submission.setTakenAt(LocalDateTime.now());
        quiz.addSubmission(submission);
        student.addQuizSubmission(submission);

        QuizSubmission saved = quizSubmissionRepository.save(submission);
        log.info("Student id={} took quiz id={} score={}", studentId, quizId, score);
        return toQuizSubmissionResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<QuizSubmissionResponse> listStudentQuizResults(Long studentId) {
        return quizSubmissionRepository.findByStudentId(studentId).stream()
                .map(this::toQuizSubmissionResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<StudentQuizResultView> listStudentQuizResultsView(Long studentId) {
        return quizSubmissionRepository.findByStudentId(studentId).stream()
                .map(this::toStudentQuizResultView)
                .toList();
    }

    private QuizResponse toQuizResponse(Quiz quiz) {
        return new QuizResponse(
                quiz.getId(),
                quiz.getModule() != null ? quiz.getModule().getId() : null,
                quiz.getTitle(),
                quiz.getTimeLimit()
        );
    }

    private QuestionResponse toQuestionResponse(Question question) {
        List<AnswerOptionResponse> options = question.getOptions().stream()
                .map(option -> new AnswerOptionResponse(option.getId(), option.getText(), option.isCorrect()))
                .toList();
        return new QuestionResponse(
                question.getId(),
                question.getText(),
                question.getType(),
                options
        );
    }

    private QuizSubmissionResponse toQuizSubmissionResponse(QuizSubmission submission) {
        return new QuizSubmissionResponse(
                submission.getId(),
                submission.getQuiz() != null ? submission.getQuiz().getId() : null,
                submission.getStudent() != null ? submission.getStudent().getId() : null,
                submission.getScore(),
                submission.getTakenAt()
        );
    }

    private StudentQuizResultView toStudentQuizResultView(QuizSubmission submission) {
        return new StudentQuizResultView(
                submission.getQuiz() != null ? submission.getQuiz().getId() : null,
                submission.getQuiz() != null ? submission.getQuiz().getTitle() : null,
                submission.getScore(),
                submission.getTakenAt()
        );
    }
}
