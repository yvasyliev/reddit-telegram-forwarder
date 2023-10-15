package com.github.yvasyliev.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class BlockedAuthor {
    @Id
    private String username;

    public BlockedAuthor() {
    }

    public BlockedAuthor(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
