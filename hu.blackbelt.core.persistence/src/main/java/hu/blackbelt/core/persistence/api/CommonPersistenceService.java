package hu.blackbelt.core.persistence.api;

import javax.persistence.EntityManager;
import java.util.List;

public interface CommonPersistenceService {
    <T> List<T> listAll(Class T);
}
