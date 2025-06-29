package com.exalt.warehousing.shared.common;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Base Entity Service
 * 
 * Generic service interface providing common CRUD operations for entities extending BaseEntity.
 * This service encapsulates standard data access patterns and business logic.
 * 
 * @param <T> Entity type extending BaseEntity
 * @param <ID> Primary key type (typically String for UUID)
 * 
 * @author Warehousing Development Team
 * @version 1.0.0
 * @since 2023-12-01
 */
public abstract class BaseEntityService<T extends BaseEntity, ID> {

    /**
     * Get the repository for this service
     * 
     * @return JpaRepository for the entity
     */
    protected abstract JpaRepository<T, ID> getRepository();

    /**
     * Find entity by ID
     * 
     * @param id Entity ID
     * @return Optional containing entity if found
     */
    @Transactional(readOnly = true)
    public Optional<T> findById(ID id) {
        return getRepository().findById(id);
    }

    /**
     * Find all entities
     * 
     * @return List of all entities
     */
    @Transactional(readOnly = true)
    public List<T> findAll() {
        return getRepository().findAll();
    }

    /**
     * Find entities with pagination
     * 
     * @param pageable Pagination parameters
     * @return Page of entities
     */
    @Transactional(readOnly = true)
    public Page<T> findAll(Pageable pageable) {
        return getRepository().findAll(pageable);
    }

    /**
     * Save entity
     * 
     * @param entity Entity to save
     * @return Saved entity
     */
    @Transactional
    public T save(T entity) {
        return getRepository().save(entity);
    }

    /**
     * Save multiple entities
     * 
     * @param entities List of entities to save
     * @return List of saved entities
     */
    @Transactional
    public List<T> saveAll(List<T> entities) {
        return getRepository().saveAll(entities);
    }

    /**
     * Update entity
     * 
     * @param entity Entity to update
     * @return Updated entity
     */
    @Transactional
    public T update(T entity) {
        return getRepository().save(entity);
    }

    /**
     * Delete entity by ID
     * 
     * @param id Entity ID to delete
     */
    @Transactional
    public void deleteById(ID id) {
        getRepository().deleteById(id);
    }

    /**
     * Delete entity
     * 
     * @param entity Entity to delete
     */
    @Transactional
    public void delete(T entity) {
        getRepository().delete(entity);
    }

    /**
     * Soft delete entity by ID
     * 
     * @param id Entity ID to soft delete
     * @param deletedBy Username of user performing deletion
     * @return true if entity was found and soft deleted, false otherwise
     */
    @Transactional
    public boolean softDeleteById(ID id, String deletedBy) {
        Optional<T> entityOpt = findById(id);
        if (entityOpt.isPresent()) {
            T entity = entityOpt.get();
            entity.softDelete(deletedBy);
            save(entity);
            return true;
        }
        return false;
    }

    /**
     * Restore soft-deleted entity by ID
     * 
     * @param id Entity ID to restore
     * @return true if entity was found and restored, false otherwise
     */
    @Transactional
    public boolean restoreById(ID id) {
        Optional<T> entityOpt = findById(id);
        if (entityOpt.isPresent()) {
            T entity = entityOpt.get();
            entity.restore();
            save(entity);
            return true;
        }
        return false;
    }

    /**
     * Check if entity exists by ID
     * 
     * @param id Entity ID to check
     * @return true if entity exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean existsById(ID id) {
        return getRepository().existsById(id);
    }

    /**
     * Count all entities
     * 
     * @return Total count of entities
     */
    @Transactional(readOnly = true)
    public long count() {
        return getRepository().count();
    }

    /**
     * Find entity by ID and throw exception if not found
     * 
     * @param id Entity ID
     * @return Entity if found
     * @throws RuntimeException if entity not found
     */
    @Transactional(readOnly = true)
    public T findByIdOrThrow(ID id) {
        return findById(id)
            .orElseThrow(() -> new RuntimeException("Entity not found with id: " + id));
    }

    /**
     * Find entity by ID, ensuring it's active (not soft deleted)
     * 
     * @param id Entity ID
     * @return Optional containing active entity if found
     */
    @Transactional(readOnly = true)
    public Optional<T> findActiveById(ID id) {
        return findById(id)
            .filter(BaseEntity::isActive);
    }

    /**
     * Find active entity by ID and throw exception if not found
     * 
     * @param id Entity ID
     * @return Active entity if found
     * @throws RuntimeException if entity not found or is soft deleted
     */
    @Transactional(readOnly = true)
    public T findActiveByIdOrThrow(ID id) {
        return findActiveById(id)
            .orElseThrow(() -> new RuntimeException("Active entity not found with id: " + id));
    }
}
