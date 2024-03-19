package com.hitit.gitapi;

import com.hitit.gitapi.api.GitHubAPIClient;
import com.hitit.gitapi.database.ContributerDAO;
import com.hitit.gitapi.database.RepositoryDAO;
import com.hitit.gitapi.models.ContributorEntity;
import com.hitit.gitapi.models.RepositoryEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);


    public static void main( String[] args )
    {

        String usernameGit = "fahadakhtar858";
        String token01 = "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";
        String orgnization = "apache";
        HashMap<String, ArrayList<ContributorEntity>> map = new HashMap<>();

        GitHubAPIClient apiClient = new GitHubAPIClient();
        try {
            map = apiClient.getRepo(token01,orgnization);
        } catch (IOException | InterruptedException e) {
            logger.error("Error occurred while fetching GitHub data: {}", e.getMessage());
            throw new RuntimeException(e);

        }

        //Data Insertion
            EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("git-api");
            EntityManager entityManager = entityManagerFactory.createEntityManager();

            try{
                RepositoryDAO repositoryDAO = new RepositoryDAO(entityManager);
                ContributerDAO contributerDAO = new ContributerDAO(entityManager);

                for(String repo: map.keySet()){
                    RepositoryEntity repoEntity = new RepositoryEntity(repo);
                    repositoryDAO.saveRepository(repoEntity);

                    List<ContributorEntity> contributorEntities = map.get(repo);
                    for(ContributorEntity ent:contributorEntities){
                        ent.setRepository(repoEntity);
                        contributerDAO.saveContributor(ent);
                    }
                }
                logger.info("Data Inserted Successfully");

                List<ContributorEntity> list = contributerDAO.findAllContributors();

                for(ContributorEntity obj:list){
                    logger.info("ID: {}, Repo: {}, Username: {}, Location: {}, Company: {}, Contributions: {}",
                            obj.getId(), obj.getRepository().getName(), obj.getUsername(), obj.getLocation(), obj.getCompany(), obj.getContributions());
                }
            }catch(Exception e) {
                logger.error("Error occurred: {}", e.getMessage());
                e.printStackTrace();
            }finally {
                // Close EntityManager and EntityManagerFactory
                entityManager.close();
                entityManagerFactory.close();
            }
        }
}
