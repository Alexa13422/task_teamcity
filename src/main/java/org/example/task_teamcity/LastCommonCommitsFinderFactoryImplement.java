package org.example.task_teamcity;

import org.example.task_teamcity.intarface.LastCommonCommitsFinder;
import org.example.task_teamcity.intarface.LastCommonCommitsFinderFactory;

public class LastCommonCommitsFinderFactoryImplement implements LastCommonCommitsFinderFactory {

    public LastCommonCommitsFinder create(String owner, String repo, String token){
        return new LastCommonCommitsFinderImplement(owner, repo, token);
    }

}
