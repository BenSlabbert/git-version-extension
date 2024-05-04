/* Licensed under Apache-2.0 2024. */
package github.benslabbert.mvn.extension.gitversion;

import static org.eclipse.jgit.lib.Constants.R_TAGS;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.slf4j.Logger;

@Component(role = GitVersion.class)
class GitVersionImpl implements GitVersion {

  private static final String DEFAULT_VERSION = "0.0.0-SNAPSHOT";

  @Requirement private Logger logger;

  GitVersionImpl() {}

  @Override
  public String getRevision() throws IOException {
    File gitDir =
        new FileRepositoryBuilder()
            .setWorkTree(new File("."))
            .readEnvironment()
            .findGitDir()
            .getGitDir();

    try (Repository repo = FileRepositoryBuilder.create(gitDir)) {
      String branch = repo.getBranch();
      logger.info("running on branch: {}", branch);

      // if we are on a detached HEAD the branch name is a commit hash
      Optional<ObjectId> resolve = resolve(repo, branch);
      if (resolve.isPresent()) {
        logger.info("we can resolve this branch name as a commit");
        List<Ref> refs = repo.getRefDatabase().getRefsByPrefix(R_TAGS);
        if (refs.isEmpty()) {
          logger.info("no tags yet, use default version");
          return DEFAULT_VERSION;
        }

        for (Ref ref : refs) {
          ObjectId objectId = ref.getObjectId();
          if (null != objectId && objectId.equals(resolve.get())) {
            logger.info("found matching tag: {} ", ref.getName());
            return ref.getName().substring(R_TAGS.length());
          }
        }

        logger.info("no matching refs found for hash: {}", branch);
      }

      boolean useSnapshotVersion = !"main".equals(branch);

      if (useSnapshotVersion) {
        logger.warn("not on main branch, using SNAPSHOT version");
        return branch + "-SNAPSHOT";
      }

      if (isNotClean(repo)) {
        logger.warn("branch is not clean, using SNAPSHOT version");
        return DEFAULT_VERSION;
      }

      List<Ref> refs = repo.getRefDatabase().getRefsByPrefix(R_TAGS);
      if (refs.isEmpty()) {
        return DEFAULT_VERSION;
      }

      Ref lastTag = refs.get(refs.size() - 1);
      return lastTag.getName().substring(R_TAGS.length());
    }
  }

  private Optional<ObjectId> resolve(Repository repo, String hash) {
    try {
      return Optional.of(repo.resolve(hash));
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  private boolean isNotClean(Repository repo) {
    try {
      Git git = new Git(repo);
      Status status = git.status().call();
      return !status.isClean();
    } catch (GitAPIException e) {
      throw new IllegalStateException(e);
    }
  }
}