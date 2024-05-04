/* Licensed under Apache-2.0 2024. */
package github.benslabbert.mvn.extension.gitversion;

import java.io.IOException;

interface GitVersion {

  String getRevision() throws IOException;
}
