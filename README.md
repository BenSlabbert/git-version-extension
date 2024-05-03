# Git Version Extension

Licensed under Apache 2.0 https://www.apache.org/licenses/LICENSE-2.0.txt

This is a maven extension to set a build version based off of the last git tag.

This is a very naive implementation and does not handle all edge cases.

## Usage

Add the following to your `.mvn/extensions.xml`:

```xml
<extensions>
  <extension>
    <groupId>com.github.benslabbert</groupId>
    <artifactId>git-version-extension</artifactId>
    <version>VERSION</version>
  </extension>
</extensions>
```
