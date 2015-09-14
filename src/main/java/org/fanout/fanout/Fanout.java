//    Fanout.java
//    ~~~~~~~~~
//    This module implements the Fanout class.
//    :authors: Konstantin Bokarius.
//    :copyright: (c) 2015 by Fanout, Inc.
//    :license: MIT, see LICENSE for more details.

package org.fanout.fanout;

import java.util.Map;
import javax.xml.bind.DatatypeConverter;

/**
 * The Fanout class is used for publishing messages to Fanout.io.
 * SSL can either be enabled or disabled. As a convenience, the realm and key
 * can also be configured by setting the 'FANOUT_REALM' and 'FANOUT_KEY'
 * environmental variables. Note that unlike the PubControl class
 * there is no need to call the finish method manually, as it will
 * automatically be called when the calling program exits.
 */
public class Fanout {
    private static ThreadLocal<PubControlClient> threadLocal =
            new ThreadLocal<PubControlClient>();
    private String realm;
    private String key;
    private boolean ssl;

    /**
     * Initialize with a specified realm, key, and an SSL switch.
     * Note that if the realm and key
     * are omitted then the initialize method will use the 'FANOUT_REALM'
     * and 'FANOUT_KEY' environmental variables.
     */
    public Fanout(String realm, String key, boolean ssl) {
        String realmEnvValue = null;
        String keyEnvValue = null;
        Map<String, String> env = System.getenv();
        for (String envName : env.keySet()) {
            if (envName == "FANOUT_REALM")
                realmEnvValue = env.get(envName);
            else if (envName == "FANOUT_KEY")
                keyEnvValue = env.get(envName);
        }

        if (realm != null)
            realm = realmEnvValue;
        if (key != null)
            key = keyEnvValue;

        this.realm = realm;
        this.key = key;
        this.ssl = ssl;
    }

    /**
     * Synchronously publish data to the specified channels.
     * Optionally provide an ID and previous
     * ID to send along with the message.
     */
    public void publish(List<String> channels, String id, String prevId)
            throws PublishFailedException {
        PubControlClient client = this.getPubControl();
        client.publish(channels, new Item(new JsonObjectFormat(data),
                id, prev_id));
    }

    /**
     * Asynchronously publish data to the specified channel.
     * Optionally provide an ID and previous ID
     * to send along with the message, as well a callback method that will be
     * called after publishing is complete and passed the result and error message
     * if an error was encountered.
     */
    public void publishAsync(List<String> channels, String id, String prevId,
            PublishCallback callback) throws PublishFailedException {
        PubControlClient client = this.getPubControl();
        client.publishAsync(channels, new Item(new JsonObjectFormat(data),
                id, prev_id, callback));
    }

    /**
     * An internal method used for retrieving the PubControl instance. The
     * PubControl instance is saved as a thread variable and if an instance
     * is not available when this method is called then one will be created.
     */
    private PubControl getPubControl() {
        if (this.threadLocal.get() == null) {
            scheme = "http";
            if (this.ssl)
                scheme = "https";
            PubControlClient client = new PubControlClient(scheme +
                    "://api.fanout.io/realm/" + this.realm);
            Map<String, Object> claims = new HashMap<String, Object>();
            client.setAuthJwt(claims, DatatypeConverter.parseBase64Binary(
                    this.key));
            this.threadLocal.set(client);
        }
        return this.threadLocal.get();
    }
}
