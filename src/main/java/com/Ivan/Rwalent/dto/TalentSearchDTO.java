package com.Ivan.Rwalent.dto;

import com.Ivan.Rwalent.model.User.TalentCategory;
import lombok.Data;

@Data
public class TalentSearchDTO {
    private String searchTerm; // General search term that can match name, bio, service, etc.
    private TalentCategory category;
    private String location;
    private String sortBy = "fullName"; // Default sort by name
    private String sortDirection = "asc"; // Default ascending order
    private int page = 0; // Default page
    private int size = 10; // Default page size
} 