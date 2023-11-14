package com.github.yvasyliev.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;

@Entity
public class RedditTelegramForwarderProperty {
    @Id
    @Enumerated(EnumType.STRING)
    private RedditTelegramForwarderPropertyName name;

    @Column(nullable = false)
    private String value;

    public RedditTelegramForwarderProperty() {
    }

    public RedditTelegramForwarderProperty(RedditTelegramForwarderPropertyName name, String value) {
        this.name = name;
        this.value = value;
    }

    public RedditTelegramForwarderPropertyName getName() {
        return name;
    }

    public void setName(RedditTelegramForwarderPropertyName name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "RedditTelegramForwarderProperty{" +
                "name=" + name +
                ", value='" + value + '\'' +
                '}';
    }
}
