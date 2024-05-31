package com.github.yvasyliev.service.data;

import com.github.yvasyliev.model.entities.RedditTelegramForwarderProperty;
import com.github.yvasyliev.model.entities.RedditTelegramForwarderPropertyName;
import com.github.yvasyliev.service.repository.RedditTelegramForwarderPropertyRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RedditTelegramForwarderPropertyService {
    @Autowired
    private RedditTelegramForwarderPropertyRepository propertyRepository;

    public Optional<Integer> findLastCreated() {
        return propertyRepository.findLastCreated();
    }

    @Transactional
    public RedditTelegramForwarderProperty saveLastCreated(int lastCreated) {
        var existingLastCreated = findLastCreated().orElse(0);
        if (existingLastCreated < lastCreated) {
            return propertyRepository.saveAndFlush(new RedditTelegramForwarderProperty(
                    RedditTelegramForwarderPropertyName.LAST_CREATED,
                    String.valueOf(lastCreated))
            );
        }
        return null;
    }
}
