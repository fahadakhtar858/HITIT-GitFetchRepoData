package com.hitit.gitapi.api;

import com.hitit.gitapi.models.ContributorEntity;

import com.hitit.gitapi.models.RepositoryEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class GitHubAPIClient {
    private static HashMap<String, ArrayList<ContributorEntity>> map = new HashMap<>();
    private static ArrayList<String> repoNameList = new ArrayList<>();
    private static ArrayList<String> contributorURLList = new ArrayList<>();
    public static HashMap<String, ArrayList<ContributorEntity>> getRepo(String token, String orgnization) throws IOException, InterruptedException {

        //URL to get top 5 repos based on Stars
        String url = "https://api.github.com/search/repositories?q=org:" + orgnization + "&sort=stars&order=desc&per_page=5";
                //Open Connection
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet(url);
        request.setHeader(HttpHeaders.AUTHORIZATION, "token"+token);
        request.setHeader(HttpHeaders.ACCEPT,"application/vnd.github.v3+json");

        HttpResponse response = client.execute(request);
        if(response.getStatusLine().getStatusCode() == 200){
            String jsonResponse = EntityUtils.toString(response.getEntity());
            JSONObject jsonObj = new JSONObject(jsonResponse);
            JSONArray items = jsonObj.getJSONArray("items");

            for (int i = 0; i < items.length(); i++) {
                JSONObject repo = items.getJSONObject(i);
                // Parsing the JSON response to get repository URLs
                String repoName = repo.getString("name");
                repoNameList.add(repoName);
                String contibutorURL = repo.getString("contributors_url");
                contributorURLList.add(contibutorURL);
                int n = i + 1;
                System.out.println("Repository No " + n + " :");
                System.out.println("Repo Name:");
                System.out.println(repoName);
                System.out.println("Contibutor List");
                map.put(repoName, fetchTopContributors(contibutorURL, token, repoName));

            }

        }else {
            System.err.println("Failed to fetch user data. Status code: " + response.getStatusLine().getStatusCode());
        }
        client.close();
        return map;



        }

    private static ArrayList<ContributorEntity> fetchTopContributors(String contibutorURL, String token, String repoName) throws IOException, InterruptedException {
        ArrayList<ContributorEntity> contributorList = new ArrayList<>();
        Thread.sleep(1000);
        URL url = new URL(contibutorURL+"?per_page=10");
        //Open Connection.

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        //Setting Request Header
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "token"+ token);
        connection.setRequestProperty("Accept", "application/vnd.github.v3+json");

        //Reading the response
        // Reading the response headers to check rate limits

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while((inputLine=in.readLine())!=null){
            response.append(inputLine);
        }
        in.close();

        //Parsing Json
        JSONArray contributorArray = new JSONArray(response.toString());
        RepositoryEntity ent = new RepositoryEntity(repoName);
        for(int i =0; i <Math.min(10,contributorArray.length()); i++){
            JSONObject contributor = contributorArray.getJSONObject(i);
            String detailUrl = contributor.getString("url");
            int contributions = contributor.getInt("contributions");//contributor.get("contributions");

            ContributorEntity entity = getDetails(detailUrl, token, repoName,contributions, ent);
            contributorList.add(entity);
            int a = i+1;
            System.out.println("Contributer "+a+" :");
            System.out.println("Username :"+entity.getUsername());
            System.out.println("Company :"+entity.getCompany());
            System.out.println("Location :"+entity.getLocation());
            System.out.println("Contributions :"+entity.getContributions());

        }
        connection.disconnect();

        return contributorList;


    }

    private static ContributorEntity getDetails(String detailUrl, String token, String repoName, int contributions, RepositoryEntity ent) throws IOException, InterruptedException {
        URL url = new URL(detailUrl);
        //Open Connection.
        Thread.sleep(1000);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
       // requestCounter++;

        //Setting Request Header
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "token"+ token);
        connection.setRequestProperty("Accept", "application/vnd.github.v3+json");

        //Reading the response

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while((inputLine=in.readLine())!=null){
            response.append(inputLine);
        }
        in.close();
        JSONObject details = new JSONObject(response.toString());//contributorArray.getJSONObject(0);
        String name = details.getString("login");
        String company = details.get("company").toString();
        String location = details.get("location").toString();
        connection.disconnect();
        return (new ContributorEntity(name,location,company,contributions,ent));
    }
}
