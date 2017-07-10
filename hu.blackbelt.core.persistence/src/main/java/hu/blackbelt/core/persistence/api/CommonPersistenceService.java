package hu.blackbelt.core.persistence.api;

import com.querydsl.sql.SQLQueryFactory;

import javax.persistence.EntityManager;
import java.util.List;

public interface CommonPersistenceService {
    SQLQueryFactory getQueryFactory();
}
