/* Licensed under Apache-2.0 2024. */
package com.example.parent;

import static org.apache.maven.plugins.annotations.LifecyclePhase.GENERATE_SOURCES;
import static org.eclipse.jgit.lib.Constants.HEAD;
import static org.eclipse.jgit.lib.Constants.R_TAGS;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

@Mojo(name = "get-version", defaultPhase = GENERATE_SOURCES)
public class GitVersionMojo extends AbstractMojo {

  @Parameter(property = "project", readonly = true)
  private MavenProject project;

  @Override
  public void execute() throws MojoExecutionException {
    String version = "1.2.3";
    getLog().info("Git hash: " + version);

    project.getProperties().put("exampleVersion", version);
    getLog().info("################# Properties #################");
    project.getProperties().forEach((k, v) -> getLog().info(k + ": " + v));
    getLog().info("################# Properties #################");

    File basedir = project.getBasedir();
    getLog().info("basedir: " + basedir);
    File gitDir = Paths.get(basedir.toURI()).resolve(".git").toFile();
    getLog().info(".git: " + gitDir);

    try (Repository repo = FileRepositoryBuilder.create(gitDir)) {
      getLog().info("Branch: " + repo.getBranch());

      try (RevWalk revWalk = new RevWalk(repo)) {
        ObjectId head = repo.resolve(HEAD);
        revWalk.markStart(revWalk.parseCommit(head));

        for (RevCommit commit : revWalk) {
          getLog().info("Commit: " + commit.getFullMessage());
        }
      }

      getLog().info("tags:");
      for (Ref ref : repo.getRefDatabase().getRefsByPrefix(R_TAGS)) {
        getLog().info("Tag: " + ref.getName());
      }
    } catch (IOException e) {
      throw new MojoExecutionException(e);
    }
  }
}
