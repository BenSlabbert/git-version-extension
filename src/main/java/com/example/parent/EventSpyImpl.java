/* Licensed under Apache-2.0 2024. */
package com.example.parent;

import java.io.IOException;
import java.util.Map;
import org.apache.maven.eventspy.AbstractEventSpy;
import org.apache.maven.eventspy.EventSpy;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.slf4j.Logger;

@Component(role = EventSpy.class)
public class EventSpyImpl extends AbstractEventSpy {

  @Requirement private Logger logger;
  @Requirement private GitVersion gitVersion;

  @Override
  public void init(Context context) throws IOException {
    logger.info("updating user properties");
    Map<String, Object> data = context.getData();
    @SuppressWarnings("unchecked")
    Map<String, Object> userProperties = (Map<String, Object>) data.get("userProperties");

    String existingRevision = (String) userProperties.get("revision");
    if (existingRevision != null) {
      logger.info("revision already set: {}", existingRevision);
      return;
    }

    String revision = gitVersion.getRevision();
    logger.info("calculated version: {}", revision);
    userProperties.putIfAbsent("revision", revision);
  }
}
