package com.github.yvasyliev.service.data;

import com.github.yvasyliev.model.entities.BlockedAuthor;
import com.github.yvasyliev.service.repository.BlockedAuthorRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BlockedAuthorService {
    @Autowired
    private BlockedAuthorRepository blockedAuthorRepository;

    public boolean isBlocked(String username) {
        return blockedAuthorRepository.existsById(username);
    }

    @Transactional
    public BlockedAuthor saveBlockedAuthor(String username) {
        return blockedAuthorRepository.save(new BlockedAuthor(username));
    }

    @Transactional
    public void removeBlockedAuthor(String username) {
        blockedAuthorRepository.deleteById(username);
    }
}
