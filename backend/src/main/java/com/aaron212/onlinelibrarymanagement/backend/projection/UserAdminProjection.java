package com.aaron212.onlinelibrarymanagement.backend.projection;

import com.aaron212.onlinelibrarymanagement.backend.model.User;
import java.sql.Timestamp;

public interface UserAdminProjection {
    Long getId();

    String getUsername();

    String getEmail();

    User.Role getRole();

    Timestamp getCreatedTime();

    Timestamp getLastUpdateTime();
}
