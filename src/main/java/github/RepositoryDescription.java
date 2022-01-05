package github;

import java.util.List;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;

public class RepositoryDescription {
    private String name;
    private GHRepository repository;
    private List<GHPullRequest> pullRequests;

    public RepositoryDescription(String name, GHRepository repository,
                                 List<GHPullRequest> pullRequests) {
        this.name = name;
        this.repository = repository;
        this.pullRequests = pullRequests;
    }

    public String getName() {
        return name;
    }

    public GHRepository getRepository() {
        return repository;
    }

    public List<GHPullRequest> getPullRequests() {
        return pullRequests;
    }
}
