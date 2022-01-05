package github;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;
import org.kohsuke.github.GHMyself;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

public class GithubJob {

    private final GitHub gitHub;
    private final Gui gui = new Gui();
    private final Set<Long> allPrIds = new HashSet<>();

    public GithubJob() {
        try {
            gitHub = new GitHubBuilder()
                .withAppInstallationToken(System.getenv("GITHUB_TOKEN"))
                .build();
            init();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void init() throws IOException {
        GHMyself myself = gitHub.getMyself();
        String login = myself.getLogin();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    HashSet<GHPullRequest> newPrs = new HashSet<>();
                    myself.getAllRepositories()
                        .values().stream()
                        .map(repo -> {
                            try {
                                List<GHPullRequest> pullRequests = repo.queryPullRequests()
                                    .list()
                                    .toList();
                                Set<Long> prIds = pullRequests.stream()
                                    .map(GHPullRequest::getId)
                                    .collect(Collectors.toSet());
                                prIds.removeAll(allPrIds);
                                allPrIds.addAll(prIds);
                                pullRequests.forEach(pr -> {
                                    if (prIds.contains(pr.getId())) {
                                        newPrs.add(pr);
                                    }
                                });
                                return new RepositoryDescription(repo.getFullName(), repo, pullRequests);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .collect(Collectors.toList());
                    newPrs.forEach(pr -> gui.showNotification("New PR in " + pr.getRepository().getFullName(),
                        pr.getTitle()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }, 1000, 1000);
    }
}
