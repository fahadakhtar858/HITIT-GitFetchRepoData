package com.hitit.gitapi.database;

import com.hitit.gitapi.models.RepositoryEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class RepositoryDAO {
    private EntityManager entityManager;

    public RepositoryDAO(EntityManager entityManager){
        this.entityManager = entityManager;//new H2Database().getEntityManager();
    }
    //Crud Operations
    public void saveRepository(RepositoryEntity repository){
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.persist(repository);
        transaction.commit();
        System.out.println("Record Inserted");
    }

    public List<RepositoryEntity> findAllRepositories(){
        System.out.println("Getting all the Data.");
        return entityManager.createQuery("SELECT r from RepositoryEntity r", RepositoryEntity.class).getResultList();
    }
    public void updateRepository(RepositoryEntity repository){
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.merge(repository);
        transaction.commit();
    }
    public void deleteRepository(RepositoryEntity repository){
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.remove(entityManager.contains(repository)?repository:entityManager.merge(repository));
        transaction.commit();
    }
    public RepositoryEntity findRepositoryByName(String name) {
        return entityManager.createQuery("SELECT r FROM RepositoryEntity r WHERE r.name = :name", RepositoryEntity.class)
                .setParameter("name", name)
                .getSingleResult();
    }

}
