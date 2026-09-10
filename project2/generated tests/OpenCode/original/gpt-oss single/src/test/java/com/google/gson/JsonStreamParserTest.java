import com.google.gson.*;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.MalformedJsonException;
import org.junit.jupiter.api.*;
import java.io.*;
import java.util.NoSuchElementException;

class JsonStreamParserTest {
    @Test
    void hasNextReturnsTrueUntilEnd() {
        JsonStreamParser parser = new JsonStreamParser("[1, 2, 3]");
        Assertions.assertTrue(parser.hasNext(), "should have next before reading all elements");
        // consume first element
        parser.next();
        Assertions.assertTrue(parser.hasNext(), "should have next after first element");
        parser.next();
        Assertions.assertTrue(parser.hasNext(), "should have next after second element");
        parser.next();
        Assertions.assertFalse(parser.hasNext(), "should not have next after last element");
    }

    @Test
    void hasNextFalseWhenDocumentComplete() {
        JsonStreamParser parser = new JsonStreamParser(""); // empty input results in END_DOCUMENT
        Assertions.assertFalse(parser.hasNext(), "empty input should not have next");
    }

    @Test
    void nextReturnsCorrectElement() {
        JsonStreamParser parser = new JsonStreamParser("{\"x\": 10}");
        JsonElement elem = parser.next();
        Assertions.assertTrue(elem.isJsonObject(), "element should be object");
        Assertions.assertEquals(10, elem.getAsJsonObject().get("x").getAsInt());
    }

    @Test
    void nextThrowsNoSuchElementExceptionWhenNone() {
        JsonStreamParser parser = new JsonStreamParser("{\"x\": 1}");
        parser.next(); // consume element
        Assertions.assertThrows(NoSuchElementException.class, parser::next, "should throw on second next");
    }

    @Test
    void hasNextThrowsJsonSyntaxExceptionOnMalformedInput() {
        // malformed JSON: opening brace without closing
        String malformed = "{\n";
        JsonStreamParser parser = new JsonStreamParser(malformed);
        Assertions.assertThrows(JsonSyntaxException.class, parser::hasNext, "malformed JSON should throw JsonSyntaxException");
    }

    @Test
    void removeThrowsUnsupportedOperationException() {
        JsonStreamParser parser = new JsonStreamParser("{});");
        Assertions.assertThrows(UnsupportedOperationException.class, parser::remove, "remove should not be supported");
    }
}
