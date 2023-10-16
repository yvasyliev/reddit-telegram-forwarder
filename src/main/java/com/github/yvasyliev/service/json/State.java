package com.github.yvasyliev.service.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.SortedSet;
import java.util.TreeSet;

@Service
@JsonIgnoreProperties(value = {"objectMapper", "stateSrc"}, ignoreUnknown = true)
public class State {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private File stateSrc;

    private int lastCreated;

    private SortedSet<String> blockedAuthors = new TreeSet<>();

    public int getLastCreated() {
        return lastCreated;
    }

    public void setLastCreated(int lastCreated) throws IOException {
        this.lastCreated = lastCreated;
        save();
    }

    public SortedSet<String> getBlockedAuthors() {
        return blockedAuthors;
    }

    public void setBlockedAuthors(SortedSet<String> blockedAuthors) throws IOException {
        this.blockedAuthors = blockedAuthors;
        save();
    }

    public void addBlockedAuthor(String author) throws IOException {
        this.blockedAuthors.add(author);
        save();
    }

    public void removeBlockedAuthor(String author) throws IOException {
        this.blockedAuthors.remove(author);
        save();
    }

    private void save() throws IOException {
        if (objectMapper != null) {
            objectMapper.writeValue(stateSrc, this);
        }
    }
}
