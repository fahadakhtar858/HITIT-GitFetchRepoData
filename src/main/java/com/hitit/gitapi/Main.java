package com.hitit.gitapi;

import com.hitit.gitapi.api.GitHubAPIClient;
import com.hitit.gitapi.database.ContributerDAO;
import com.hitit.gitapi.database.RepositoryDAO;
import com.hitit.gitapi.models.ContributorEntity;
import com.hitit.gitapi.models.RepositoryEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main {
    private static int requestCounter = 0;


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
            e.printStackTrace();
            throw new RuntimeException(e);

        }

            /* Demo Insertions.
            ArrayList<ContributorEntity>arr = new ArrayList<>();
            RepositoryEntity ent01 = new RepositoryEntity("echarts");
            ContributorEntity test01 = new ContributorEntity("pissang", "Shanghai, China", "null",3156, ent01);
            ContributorEntity test02 = new ContributorEntity("Fahad", "Lahore, Pakistan", "Abacus",2000, ent01);
            arr.add(test01);
            arr.add(test02);


        map.put(ent01.getName(),arr);
        Demo Insertions End here.*/

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
                System.out.println("Data Inserted Successfully");

                List<ContributorEntity> list = contributerDAO.findAllContributors();

                for(ContributorEntity obj:list){
                    System.out.println("ID:"+obj.getId()+", Repo:"+obj.getRepository().getName()+", Username:"+obj.getUsername()
                    +", Location:"+obj.getLocation()+", Company:"+obj.getCompany()+", Contributions:"+obj.getContributions());
                }
            }catch(Exception e) {
                e.printStackTrace();
            }finally {
                // Close EntityManager and EntityManagerFactory
                entityManager.close();
                entityManagerFactory.close();
            }
        }
}
