package com.example.bug_tracker.services;

import com.example.bug_tracker.config.CustomUserDetails;
import com.example.bug_tracker.entities.Bug;
import com.example.bug_tracker.entities.Label;
import com.example.bug_tracker.entities.Project;
import com.example.bug_tracker.repositories.BugRepository;
import com.example.bug_tracker.repositories.LabelRepository;
import com.example.bug_tracker.repositories.ProjectRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LabelService {
    private final LabelRepository labelRepository;
    private final BugRepository bugRepository;
    private final ProjectRepository projectRepository;

    @Transactional
    public Label addLabel(String bugId, @Valid Label label) {
        String userId = getCurrentUserId();
        Bug bug = bugRepository.findById(bugId)
                .orElseThrow(() -> new IllegalArgumentException("Bug not found"));
        validateProjectMembership(bug.getProjectId(), userId);
        label.setBugId(bugId);
        return labelRepository.save(label);
    }

    public List<Label> findLabelsByBugId(String bugId) {
        String userId = getCurrentUserId();
        Bug bug = bugRepository.findById(bugId)
                .orElseThrow(() -> new IllegalArgumentException("Bug not found"));
        validateProjectMembership(bug.getProjectId(), userId);
        return labelRepository.findByBugId(bugId);
    }

    private void validateProjectMembership(String projectId, String userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
        if (!project.getUserIds().contains(userId)) {
            throw new SecurityException("User is not a member of the project");
        }
    }

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new SecurityException("User not authenticated");
        }
        return ((CustomUserDetails) authentication.getPrincipal()).getId();
    }
}
