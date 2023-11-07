package com.github.yvasyliev.service.data;

import com.github.yvasyliev.model.entities.BlockedAuthor;
import com.github.yvasyliev.service.repository.BlockedAuthorRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlockedAuthorService {
    @Autowired
    private BlockedAuthorRepository blockedAuthorRepository;

    public List<BlockedAuthor> findAll() {
        return blockedAuthorRepository.findAll();
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
