import com.hitit.gitapi.api.GitHubAPIClient;
import com.hitit.gitapi.models.ContributorEntity;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class GitAPITest {
    @Test
    public void gitDataFetchingTest(){
        String usernameGit = "fahadakhtar858";
        String token01 = "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";
        String orgnization = "apache";
        GitHubAPIClient apiClient = new GitHubAPIClient();
        HashMap<String, ArrayList<ContributorEntity>> map = new HashMap<>();

        try {
            map = apiClient.getRepo(token01, orgnization);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        assertGitData(map);


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
}
