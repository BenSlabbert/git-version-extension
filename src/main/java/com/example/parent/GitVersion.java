/* Licensed under Apache-2.0 2024. */
package com.example.parent;

import java.io.IOException;

interface GitVersion {

  String getRevision() throws IOException;
}
