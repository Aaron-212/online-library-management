package com.aaron212.onlinelibrarymanagement.backend.projection;

import java.util.List;

public interface BookProjection {
    Long getId();
    String getIsbn();
    String getTitle();
    String getCoverURL();
    List<BookAuthorProjection> getBookAuthors();
}
