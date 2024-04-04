/* Licensed under Apache-2.0 2024. */
package com.example.parent;

import static org.apache.maven.plugins.annotations.LifecyclePhase.GENERATE_SOURCES;
import static org.eclipse.jgit.lib.Constants.R_TAGS;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

@Mojo(name = "get-version", defaultPhase = GENERATE_SOURCES)
public class GitVersionMojo extends AbstractMojo {

  @Parameter(property = "project", readonly = true)
  private MavenProject project;

  @Parameter(property = "get-version.masterBranch", readonly = true, defaultValue = "master")
  private String masterBranch;

  @Parameter(property = "get-version.versionProperty", readonly = true, defaultValue = "revision")
  private String versionProperty;

  private boolean useSnapshotVersion = false;

  @Override
  public void execute() throws MojoExecutionException {
    getLog().info("masterBranch: " + masterBranch);
    getLog().info("versionProperty: " + versionProperty);

    File basedir = project.getBasedir();
    File gitDir = Paths.get(basedir.toURI()).resolve(".git").toFile();
    getLog().info("opening git from: " + gitDir);

    try (Repository repo = FileRepositoryBuilder.create(gitDir)) {
      String branch = repo.getBranch();
      getLog().info("Branch: " + branch);

      if (masterBranch.equals(branch)) {
        getLog().info("currently on master branch, use a concrete version");
        useSnapshotVersion = false;
      } else {
        getLog().info("currently on a development branch, use a snapshot version");
        useSnapshotVersion = true;
      }

      Ref lastTag = repo.getRefDatabase().getRefsByPrefix(R_TAGS).getLast();
      String version = lastTag.getName().substring(R_TAGS.length());
      project.getProperties().put(versionProperty, version);
      getLog()
          .info(
              String.format(
                  "updated property: %s=%s",
                  versionProperty, project.getProperties().get(versionProperty)));

      //      // Create a new tag
      //      String tagName = "v1.0.0-" + System.currentTimeMillis();
      //      try (Git git = new Git(repo)) {
      //        git.tag().setName(tagName).call();
      //        // might need this for the remote?
      ////        git.push().setPushTags().call();
      //      } catch (GitAPIException e) {
      //        throw new MojoExecutionException("Failed to create git tag", e);
      //      }
      //
      //      getLog().info("Created git tag: " + tagName);

    } catch (IOException e) {
      throw new MojoExecutionException(e);
    }
  }
}
