package com.github.yvasyliev.service.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractService {
    @Autowired
    protected SessionFactory sessionFactory;
}
