import org.junit.Test;
import static org.junit.Assert.*;


import org.fanout.fanout.*;
import org.fanout.pubcontrol.*;
import java.util.*;

public class FanoutTest {

    private static class Callback implements PublishCallback {
        public void completed(boolean result, String message) {
            if (result)
                System.out.println("Publish successful");
            else
                System.out.println("Publish failed with message: " + message);
        }
    }

    @Test
    public void testAll() {
        // Passing null for the realm and key arguments causes Fanout to use
        // the FANOUT_REALM and FANOUT_KEY environmental variables.
        Fanout fanout = new Fanout();

        // Alternatively specify the realm and/or key.
        fanout = new Fanout("1519c80f", "h1K8NfQrSBM3j1dBz+noeQ==", true);

        // Publish across all configured endpoints:
        List<String> channels = new ArrayList<String>();
        channels.add("test_channel");

        try {
            fanout.publish(channels, "Test publish!");
        } catch (PublishFailedException exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
        }
        fanout.publishAsync(channels,
                "Test async publish!", new Callback());
    }
}
