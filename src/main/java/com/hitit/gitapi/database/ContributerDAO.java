package com.hitit.gitapi.database;

import com.hitit.gitapi.models.ContributorEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.hibernate.Session;

import java.util.List;

public class ContributerDAO {
    private EntityManager entityManager;
    public ContributerDAO(EntityManager entityManager){
        this.entityManager = entityManager;//new H2Database().getEntityManager();
    }

    public void saveContributor(ContributorEntity entity){
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.persist(entity);
        transaction.commit();
    }
    public ContributorEntity findContributorById(Long id) {
        return entityManager.find(ContributorEntity.class, id);
    }

    public List<ContributorEntity> findAllContributors() {
        return entityManager.createQuery("SELECT c FROM ContributorEntity c", ContributorEntity.class).getResultList();
    }

    public void updateContributor(ContributorEntity contributor) {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.merge(contributor);
        transaction.commit();
    }

    public void deleteContributor(ContributorEntity contributor) {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.remove(entityManager.contains(contributor) ? contributor : entityManager.merge(contributor));
        transaction.commit();
    }
}
