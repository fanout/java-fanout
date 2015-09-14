java-fanout
===========

Author: Konstantin Bokarius <kon@fanout.io>

A Java convenience library for publishing messages to Fanout.io using the EPCP protocol.

License
-------

java-fanout is offered under the MIT license. See the LICENSE file.

Installation
------------

java-fanout is compatible with JDK 6 and above.

Maven:

```xml
<dependency>
  <groupId>org.fanout</groupId>
  <artifactId>fanout</artifactId>
  <version>1.0.0</version>
</dependency>
```

HTTPS Publishing
----------------

Note that on some operating systems Java may require you to add the root CA certificate of the publishing server to the key store. This is particularly the case with OSX. Follow the steps outlined in this article to address the issue: http://nodsw.com/blog/leeland/2006/12/06-no-more-unable-find-valid-certification-path-requested-target

Also, if using Java 6 you may run into SNI issues. If this occurs we recommend HTTP-only publishing or upgrading to Java 7 or above.

Usage
-----

```java
import org.fanout.fanout.*;
import java.util.*;

public class FanoutExample {

    private static class Callback implements PublishCallback {
        public void completed(boolean result, String message) {
            if (result)
                System.out.println("Publish successful");
            else
                System.out.println("Publish failed with message: " + message);
        }
    }

    public static void main(String[] args) {

        // Passing null for the realm and key arguments causes Fanout to use
        // the FANOUT_REALM and FANOUT_KEY environmental variables.
        Fanout fanout = new Fanout(null, null, true);

        // Alternatively specify the realm and/or key.
        fanout = new Fanout("<myrealm>", "<myrealmkey>", true)

        // Publish across all configured endpoints:
        List<String> channels = new ArrayList<String>();
        channels.add("test_channel");

        try {
            pub.publish(channels, "Test publish!");
        } catch (PublishFailedException exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
        }
        pub.publishAsync(channels, "Test async publish!", new Callback());
    }
}
```
