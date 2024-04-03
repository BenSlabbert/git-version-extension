/* Licensed under Apache-2.0 2024. */
package com.example.parent;

import static org.apache.maven.plugins.annotations.LifecyclePhase.GENERATE_SOURCES;
import static org.eclipse.jgit.lib.Constants.R_TAGS;

import java.io.File;
import java.io.IOException;
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

  @Override
  public void execute() throws MojoExecutionException {
    String version = "1.2.3";
    getLog().info("Git hash: " + version);

    project.getProperties().put("exampleVersion", version);
    project.getProperties().forEach((k, v) -> getLog().info(k + ": " + v));

    File basedir = project.getBasedir();
    getLog().info("basedir: " + basedir);

    try (Repository repo = FileRepositoryBuilder.create(basedir)) {
      String branch = repo.getBranch();
      getLog().info("Branch: " + branch);
      for (Ref ref : repo.getRefDatabase().getRefsByPrefix(R_TAGS)) {
        getLog().info("Tag: " + ref.getName());
      }
    } catch (IOException e) {
      throw new MojoExecutionException(e);
    }
  }
}
