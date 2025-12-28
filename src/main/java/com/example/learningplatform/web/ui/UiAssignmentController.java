package com.example.learningplatform.web.ui;

import com.example.learningplatform.dto.AssignmentSummaryResponse;
import com.example.learningplatform.dto.SubmitAssignmentRequest;
import com.example.learningplatform.dto.UserSummaryResponse;
import com.example.learningplatform.service.AssignmentService;
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

@Controller
@RequestMapping("/ui/assignments")
public class UiAssignmentController {
    private final AssignmentService assignmentService;
    private final UserService userService;

    public UiAssignmentController(AssignmentService assignmentService, UserService userService) {
        this.assignmentService = assignmentService;
        this.userService = userService;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public String listAssignments(Model model) {
        List<AssignmentSummaryResponse> assignments = assignmentService.listAssignments();
        model.addAttribute("assignments", assignments);
        return "assignments";
    }

    @GetMapping("/{id}/submit")
    @Transactional(readOnly = true)
    public String showSubmitForm(@PathVariable Long id, Model model) {
        AssignmentSummaryResponse assignment = assignmentService.getAssignmentSummary(id);
        List<UserSummaryResponse> students = userService.listStudents();
        model.addAttribute("assignment", assignment);
        model.addAttribute("students", students);
        return "submit-assignment";
    }

    @PostMapping("/{id}/submit")
    public String submitAssignment(@PathVariable Long id,
                                   @RequestParam Long studentId,
                                   @RequestParam String content,
                                   RedirectAttributes redirectAttributes) {
        SubmitAssignmentRequest request = new SubmitAssignmentRequest(studentId, content);
        assignmentService.submitAssignment(id, studentId, request);
        redirectAttributes.addFlashAttribute("message", "Submission created successfully");
        return "redirect:/ui/assignments";
    }
}
