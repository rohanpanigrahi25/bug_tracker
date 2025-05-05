package com.example.bug_tracker.entities;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "labels")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Label {
    @Id
    private String id;

    @NotBlank(message = "Bug ID is required")
    private String bugId;

    @NotBlank(message = "Name is required")
    @Size(min = 1, max = 50, message = "Name must be between 1 and 50 characters")
    private String name;
}
