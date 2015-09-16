//    Fanout.java
//    ~~~~~~~~~
//    This module implements the Fanout class.
//    :authors: Konstantin Bokarius.
//    :copyright: (c) 2015 by Fanout, Inc.
//    :license: MIT, see LICENSE for more details.

package org.fanout.fanout;

import java.util.*;
import javax.xml.bind.DatatypeConverter;
import org.fanout.pubcontrol.*;

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
     * Initialize and have realm and key taken from environmental variables.
     * Note that the 'FANOUT_REALM' and 'FANOUT_KEY' environmental variables
     * will be used.
     */
    public Fanout() {
        this(null, null, true);
    }

    /**
     * Initialize with a specified realm and key.
     * Note that if the realm and key
     * are omitted then the initialize method will use the 'FANOUT_REALM'
     * and 'FANOUT_KEY' environmental variables.
     */
    public Fanout(String realm, String key) {
        this(realm, key, true);
    }

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
            if (envName.equals("FANOUT_REALM"))
                realmEnvValue = env.get(envName);
            else if (envName.equals("FANOUT_KEY"))
                keyEnvValue = env.get(envName);
        }

        if (realm == null)
            realm = realmEnvValue;
        if (key == null)
            key = keyEnvValue;

        this.realm = realm;
        this.key = key;
        this.ssl = ssl;
    }

    /**
     * Synchronously publish data to the specified channels.
     */
    public void publish(List<String> channels, Object data)
            throws PublishFailedException {
        this.publish(channels, data, null, null);
    }

    /**
     * Synchronously publish data to the specified channels.
     * Optionally provide an ID and previous
     * ID to send along with the message.
     */
    public void publish(List<String> channels, Object data, String id, String prevId)
            throws PublishFailedException {
        PubControlClient client = this.getPubControl();
        List<Format> formats = new ArrayList<Format>();
        formats.add(new JsonObjectFormat(data));
        client.publish(channels, new Item(formats, id, prevId));
    }

    /**
     * Asynchronously publish data to the specified channel.
     */
    public void publishAsync(List<String> channels, Object data) {
        this.publishAsync(channels, data, null, null, null);
    }

    /**
     * Asynchronously publish data to the specified channel.
     * Optionally provide a callback method that will be
     * called after publishing is complete and passed the result and error message
     * if an error was encountered.
     */
    public void publishAsync(List<String> channels, Object data, PublishCallback callback) {
        this.publishAsync(channels, data, null, null, callback);
    }

    /**
     * Asynchronously publish data to the specified channel.
     * Optionally provide an ID and previous ID
     * to send along with the message, as well a callback method that will be
     * called after publishing is complete and passed the result and error message
     * if an error was encountered.
     */
    public void publishAsync(List<String> channels, Object data, String id, String prevId,
            PublishCallback callback) {
        PubControlClient client = this.getPubControl();
        List<Format> formats = new ArrayList<Format>();
        formats.add(new JsonObjectFormat(data));
        client.publishAsync(channels, new Item(formats,
                id, prevId), callback);
    }

    /**
     * An internal method used for retrieving the PubControl instance. The
     * PubControl instance is saved as a thread variable and if an instance
     * is not available when this method is called then one will be created.
     */
    private PubControlClient getPubControl() {
        if (this.threadLocal.get() == null) {
            String scheme = "http";
            if (this.ssl)
                scheme = "https";
            final PubControlClient client = new PubControlClient(scheme +
                    "://api.fanout.io/realm/" + this.realm);
            Map<String, Object> claims = new HashMap<String, Object>();
            claims.put("iss", this.realm);
            client.setAuthJwt(claims, DatatypeConverter.parseBase64Binary(
                    this.key));
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    client.finish();
                }
            });
            this.threadLocal.set(client);
        }
        return this.threadLocal.get();
    }
}
