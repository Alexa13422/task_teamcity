package org.example.task_teamcity;

import org.example.task_teamcity.intarface.LastCommonCommitsFinder;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TaskTeamcityApplicationTests {

    private LastCommonCommitsFinder finder;

    @BeforeEach
    public void setUp() {
        String owner = "Alexa13422";
        String repo = "lastCommonCommits_test";
        String token = null;

        finder = new LastCommonCommitsFinderFactoryImplement().create(owner, repo, token);
    }

    @Test
    public void findLastCommonCommitsValidBranches() throws IOException, JSONException {
        String lastCommonCommit = finder.findLastCommonCommits("master", "dev").toString();

        assertFalse(lastCommonCommit.isEmpty(), "There should be at least one common commit");
    }

    @Test
    public void findLastCommonCommitsInvalidBranch() {
        assertThrows(IOException.class, () -> {
            finder.findLastCommonCommits("non-existent-branch", "dev");
        }, "An IOException should be thrown for non-existent branches");
    }

    @Test
    public void findLastCommonCommitsWithSameBranch() throws IOException, JSONException {
        String lastCommonCommits = finder.findLastCommonCommits("master", "master").toString();

        assertNotNull(lastCommonCommits, "The result should not be null");
        assertFalse(lastCommonCommits.isEmpty(), "There should be at least one commit");
    }

    @Test
    public void testCacheBehavior() throws IOException, JSONException {
        String branchName = "master";
        Map<String, String> firstCall = finder.findLastCommonCommits(branchName, branchName);
        Map<String, String> secondCall = finder.findLastCommonCommits(branchName, branchName);

        assertEquals(firstCall, secondCall, "Cached results should be identical");
    }
}
