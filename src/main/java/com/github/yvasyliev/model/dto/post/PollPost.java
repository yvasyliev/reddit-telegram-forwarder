package com.github.yvasyliev.model.dto.post;

import java.util.List;

public class PollPost extends Post {
    private List<String> options;

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    @Override
    public String getType() {
        return Type.POLL;
    }
}
