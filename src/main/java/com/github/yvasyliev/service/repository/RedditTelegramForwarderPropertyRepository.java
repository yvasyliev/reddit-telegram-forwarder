package com.github.yvasyliev.service.repository;

import com.github.yvasyliev.model.entities.RedditTelegramForwarderProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RedditTelegramForwarderPropertyRepository extends JpaRepository<RedditTelegramForwarderProperty, String> {
    @Query("""
            select
                r.value
            from
                RedditTelegramForwarderProperty r
            where
                r.name = com.github.yvasyliev.model.entities.RedditTelegramForwarderPropertyName.LAST_CREATED""")
    Optional<Integer> findLastCreated();
}
