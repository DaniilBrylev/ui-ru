package com.example.learningplatform.service;

import com.example.learningplatform.dto.UserSummaryResponse;
import com.example.learningplatform.entity.User;
import com.example.learningplatform.entity.UserRole;
import com.example.learningplatform.exception.NotFoundException;
import com.example.learningplatform.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<UserSummaryResponse> listStudents() {
        return userRepository.findByRole(UserRole.STUDENT).stream()
                .sorted(Comparator.comparing(User::getName))
                .map(this::toSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserSummaryResponse getStudent(Long id) {
        User user = userRepository.findById(id)
                .filter(u -> u.getRole() == UserRole.STUDENT)
                .orElseThrow(() -> new NotFoundException("Student not found: " + id));
        return toSummary(user);
    }

    private UserSummaryResponse toSummary(User user) {
        return new UserSummaryResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}
