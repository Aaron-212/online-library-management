package com.aaron212.onlinelibrarymanagement.backend.projection;

import com.aaron212.onlinelibrarymanagement.backend.model.Author;

public interface BookAuthorProjection {
    Author getAuthor();
    String getContributionType();
}
