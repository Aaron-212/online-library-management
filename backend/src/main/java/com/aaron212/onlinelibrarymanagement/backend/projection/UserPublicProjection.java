package com.aaron212.onlinelibrarymanagement.backend.projection;

import java.sql.Timestamp;

public interface UserPublicProjection {
    Long getId();
    String getUsername();
    Timestamp getCreatedTime();
}
