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

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public TalentCategory getCategory() {
        return category;
    }

    public void setCategory(TalentCategory category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}