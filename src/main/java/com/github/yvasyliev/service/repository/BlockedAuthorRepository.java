package com.github.yvasyliev.service.repository;

import com.github.yvasyliev.model.entities.BlockedAuthor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockedAuthorRepository extends JpaRepository<BlockedAuthor, String> {
}
