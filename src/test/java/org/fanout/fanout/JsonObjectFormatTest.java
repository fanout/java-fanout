import org.junit.Test;
import static org.junit.Assert.*;

import org.fanout.fanout.*;

public class JsonObjectFormatTest {
    @Test
    public void testJsonObjectFormat() {
        JsonObjectFormat format = new JsonObjectFormat("value");
        assertEquals(format.name(), "json-object");
        assertEquals(format.export(), "value");
    }
}
