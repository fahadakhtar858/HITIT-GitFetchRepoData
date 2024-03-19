import com.hitit.gitapi.api.GitHubAPIClient;
import com.hitit.gitapi.database.ContributerDAO;
import com.hitit.gitapi.database.RepositoryDAO;
import com.hitit.gitapi.models.ContributorEntity;
import com.hitit.gitapi.models.RepositoryEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class GitAPITest {
    private static final Logger logger = LoggerFactory.getLogger(GitAPITest.class);
    @Test
    public void gitDataFetchingTest(){
        String usernameGit = "fahadakhtar858";
        String token01 = "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";
        String organization = "apache";
        GitHubAPIClient apiClient = new GitHubAPIClient();
        HashMap<String, ArrayList<ContributorEntity>> map = new HashMap<>();

        try {
            map = apiClient.getRepo(token01, organization);
        } catch (IOException | InterruptedException e) {
            logger.error("Error occurred while fetching GitHub data: {}", e.getMessage());
            throw new RuntimeException(e);
        }
        assertGitData(map);
        assertOrganization(map, organization);
        assertRepositoryCount(map, 5);
        assertContributorsCount(map, 10);
        assertDatabaseWriteService(map);


    }

    private void assertGitData(HashMap<String, ArrayList<ContributorEntity>> map) {
        // Iterate through the entries of the HashMap
        for (String key : map.keySet()) {
            // Get the ArrayList associated with the current key
            ArrayList<ContributorEntity> contributors = map.get(key);

            // Assert that the ArrayList is not null
            assert contributors != null : "ArrayList associated with key '" + key + "' is null";

            // Assert that the ArrayList is not empty
            assert !contributors.isEmpty() : "ArrayList associated with key '" + key + "' is empty";

            // Iterate through the contributors in the ArrayList
            for (ContributorEntity contributor : contributors) {
                // Assert data points for each contributor
                assert contributor.getUsername() != null : "Username of contributor in ArrayList associated with key '" + key + "' is null";
                assert contributor.getRepository().getName() != null : "Repo of contributor in ArrayList associated with key '" + key + "' is null";
                assert contributor.getContributions() != 0 : "Contributions of contributor in ArrayList associated with key '" + key + "' is zero";
            }
        }

    }
    private void assertOrganization(HashMap<String, ArrayList<ContributorEntity>> map, String expectedOrganization) {
        // Check if the organization in the retrieved data matches the expected organization
        assertEquals("Organization mismatch", expectedOrganization, map.keySet().iterator().next());
    }
    private void assertRepositoryCount(HashMap<String, ArrayList<ContributorEntity>> map, int expectedCount) {
        // Check if the number of repositories is equal to the expected count
        assertEquals("Repository count mismatch", expectedCount, map.size());
    }

    private void assertContributorsCount(HashMap<String, ArrayList<ContributorEntity>> map, int expectedCount) {
        // Calculate the total number of contributors
        int totalContributors = map.values().stream().mapToInt(ArrayList::size).sum();
        // Check if the total number of contributors matches the expected count
        assertEquals("Contributors count mismatch", expectedCount, totalContributors);
    }
    private void assertDatabaseWriteService(HashMap<String, ArrayList<ContributorEntity>> map) {
        // Initialize database connection
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("git-api");
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try {
            // Initialize DAOs
            RepositoryDAO repositoryDAO = new RepositoryDAO(entityManager);
            ContributerDAO contributerDAO = new ContributerDAO(entityManager);

            // Check if repositories and contributors are present in the database
            for (String repo : map.keySet()) {
                RepositoryEntity repoEntity = repositoryDAO.findRepositoryByName(repo);
                // Assert repository existence
                assertEquals("Repository not found in database: " + repo, repo, repoEntity.getName());

                List<ContributorEntity> contributorEntities = map.get(repo);
                for (ContributorEntity ent : contributorEntities) {
                    // Assert contributor existence
                    ContributorEntity contributorEntity = contributerDAO.findContributorByUsername(ent.getUsername());
                    assertEquals("Contributor not found in database: " + ent.getUsername(), ent.getUsername(), contributorEntity.getUsername());
                }
            }

            logger.info("Data inserted successfully and verified in the database.");
        } catch (Exception e) {
            logger.error("Error occurred while checking database write service: {}", e.getMessage());
            throw new RuntimeException(e);
        } finally {
            // Close EntityManager and EntityManagerFactory
            entityManager.close();
            entityManagerFactory.close();
        }
    }
}
