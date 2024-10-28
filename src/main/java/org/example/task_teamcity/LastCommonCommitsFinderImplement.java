package org.example.task_teamcity;

import org.example.task_teamcity.intarface.LastCommonCommitsFinder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

class LastCommonCommitsFinderImplement implements LastCommonCommitsFinder {
    private final String owner;
    private final String repo;
    private final String token;
    private static final long CACHE_TIMEOUT_MS = 5 * 60 * 1000;

    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();

    public LastCommonCommitsFinderImplement(String owner, String repo, String token) {
        this.owner = owner;
        this.repo = repo;
        this.token = token;
    }

    @Override
    public Map<String, String> findLastCommonCommits(String branchA, String branchB) throws IOException {
        Map<String, String> commitsBranchA = getCommitsFromBranch(branchA);
        Map<String, String> commitsBranchB = getCommitsFromBranch(branchB);

        Map<String, String> commonCommits = new HashMap<>();
        for (Map.Entry<String, String> entry : commitsBranchA.entrySet()) {
            String key = entry.getKey();
            if (commitsBranchB.containsKey(key)) {
                commonCommits.put(key, entry.getValue()
                        .replace("T", "")
                        .replace("Z", "")
                        .trim());
            }
        }
        return commonCommits;
    }

    private Map<String, String> getCommitsFromBranch(String branch) throws IOException {
        CacheEntry cacheEntry = cache.get(branch);

        if (cacheEntry != null && (System.currentTimeMillis() - cacheEntry.timestamp < CACHE_TIMEOUT_MS)) {
            return cacheEntry.commits;
        }


        String url = String.format("https://api.github.com/repos/%s/%s/commits?sha=%s", owner, repo, branch);
        HttpURLConnection connection = createConnection(url);

        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new IOException("failed to find commits " + responseCode);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        Map<String,String> commitHash = new ConcurrentHashMap<>();
        try {
            JSONArray commitsArray = new JSONArray(response.toString());
            for (int i = 0; i < commitsArray.length(); i++) {
                JSONObject commitObj = commitsArray.getJSONObject(i);
                String sha = commitObj.getString("sha");
                String date = commitObj.getJSONObject("commit").getJSONObject("committer").getString("date");
                commitHash.put(sha, date);
            }
        } catch (JSONException e) {
            throw new IOException(e);
        }

        cache.put(branch, new CacheEntry(commitHash, System.currentTimeMillis()));

        return commitHash;
    }

    private HttpURLConnection createConnection(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        if (token != null && !token.isEmpty()) {
            connection.setRequestProperty("Authorization", "Bearer " + token);
        }
        connection.setRequestProperty("Accept", "application/vnd.github.v3+json");
        return connection;
    }

//    private String findLastCommon(Map<String, String> commonCommits, Map<String, String> branchACommits, Map<String, String> branchBCommits) {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
//        String newestCommit = "";
//        Date newestDate = null;
//        for (Map.Entry<String,String> entry : commonCommits.entrySet()) {
//            String key = entry.getKey();
//            Date date;
//            try {
//                date = dateFormat.parse(entry.getValue());
//            } catch (ParseException e) {
//                throw new RuntimeException(e);
//            }
//            if (newestDate == null || date.after(newestDate)){
//                newestDate = date;
//                newestCommit = key;
//            }
//        }
//        return newestCommit;
//    }

}
