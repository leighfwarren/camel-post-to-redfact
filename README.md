# camel-post-to-redfact
Red Fact Export.

A plugin to export articles and their associated images to Redfact.

Usage
=====

Add the following to your project pom.

```
<dependency>
  <groupId>com.atex.plugins</groupId>
  <artifactId>camel-post-to-redfact</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
<dependency>
  <groupId>com.atex.plugins</groupId>
  <artifactId>camel-post-to-redfact</artifactId>
  <version>1.0-SNAPSHOT</version>
  <classifier>contentdata</classifier>
</dependency>
```


Configure
=========

The environment is configured inside Polopoly, with the external id:
```
environment.profile
```


| Variable | Description | Value |
| ------- | ------ | --------- |
|redfact.api-uri | Redfact Http POST URL |
|redfact.username| username of api connection (unused) |
|redfact.password| password of api connection (unused) |
|redfact.onecms-image-prefix| prefix of url to onecms server |
|redfact.onecms-image-secret| SECRET |
|redfact.onecms-image-format| image format required e.g. (3x2) |


Operation
=========



