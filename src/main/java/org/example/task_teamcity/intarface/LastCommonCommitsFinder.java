package org.example.task_teamcity.intarface;

import org.json.JSONException;

import java.io.IOException;
import java.util.Map;

public interface LastCommonCommitsFinder {

    /**
     * Finds SHAs of last commits that are reachable from both
     * branchA and branchB
     *
     * @param branchA branch name (e.g. "main")
     * @param branchB branch name (e.g. "dev")
     * @return a collection of SHAs of last common commits
     * @throws IOException if any error occurs
     */
    Map<String, String> findLastCommonCommits(String branchA, String branchB) throws IOException, JSONException;

}