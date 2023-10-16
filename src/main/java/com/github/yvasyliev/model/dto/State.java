package com.github.yvasyliev.model.dto;

import java.util.SortedSet;
import java.util.TreeSet;

public class State {
    private int lastCreated;
    private SortedSet<String> blockedAuthors;

    public State() {
        this(0, new TreeSet<>());
    }

    public State(int lastCreated, SortedSet<String> blockedAuthors) {
        this.lastCreated = lastCreated;
        this.blockedAuthors = blockedAuthors;
    }

    public int getLastCreated() {
        return lastCreated;
    }

    public void setLastCreated(int lastCreated) {
        this.lastCreated = lastCreated;
    }

    public SortedSet<String> getBlockedAuthors() {
        return blockedAuthors;
    }

    public void setBlockedAuthors(SortedSet<String> blockedAuthors) {
        this.blockedAuthors = blockedAuthors;
    }
}
