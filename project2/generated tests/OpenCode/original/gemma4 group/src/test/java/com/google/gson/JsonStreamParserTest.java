package com.google;

import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonStreamParserTest {

    @Test
    void testParseMultipleElements() {
        String json = "{\"a\":1} [true, false] \"end\"";
        JsonStreamParser parser = new JsonStreamParser(json);
        List<JsonElement> elements = new ArrayList<>();

        while (parser.hasNext()) {
            elements.add(parser.next());
        }

        assertEquals(3, elements.size());
        assertEquals(1, elements.get(0).getAsJsonObject().get("a").getAsInt());
        assertTrue(elements.get(1).getAsJsonArray().get(0).getAsBoolean());
        assertEquals("end", elements.get(2).getAsString());
    }
}