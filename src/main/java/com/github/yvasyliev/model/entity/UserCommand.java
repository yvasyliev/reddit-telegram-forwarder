package com.github.yvasyliev.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class UserCommand {
    @Id
    private Long userId;

    @Column(nullable = false)
    private String command;

    public UserCommand() {
    }

    public UserCommand(Long userId, String command) {
        this.userId = userId;
        this.command = command;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
