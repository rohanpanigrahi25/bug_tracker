package com.example.bug_tracker.entities;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


@Document(collection = "comments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {
    @Id
    private String id;

    @NotBlank(message = "Bug ID is required")
    private String bugId;

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Content is required")
    @Size(max = 1000, message = "Content cannot exceed 1000 characters")
    private String content;

    @NotNull(message = "Timestamp is required")
    private LocalDateTime timestamp;
}
