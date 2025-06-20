package com.aaron212.onlinelibrarymanagement.backend.projection;

import java.sql.Timestamp;

public interface UserFullProjection {
    Long getId();

    String getUsername();

    String getEmail();

    String getRole();

    Timestamp getCreatedTime();

    Timestamp getLastUpdateTime();
}
