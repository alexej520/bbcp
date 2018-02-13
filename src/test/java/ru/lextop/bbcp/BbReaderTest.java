package ru.lextop.bbcp;

import org.junit.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class BbReaderTest {

    private BbReader alphabetReader() {
        return new BbReader("abcdefghijklmnopqrstuvwxyz");
    }

    @Test
    public void pointer() {
        BbReader reader = alphabetReader();
        int counter = 0;
        while (counter < 25) {
            counter++;
            reader.next();
        }
        assertEquals('z', reader.next());
    }

    private BbReader reader;

    private void recreate(String text) {
        recreate(text, null);
    }

    private void recreate(String text, Tag tag) {
        reader = new BbReader(text);
    }

    private void close() {
        try {
            reader.close();
        } catch (IOException e) {
            fail();
        }
    }

    @Test
    public void skipSpaces() {
        recreate("    4 sp   ");
        reader.next();
        reader.skipSpaces();
        assertEquals('4', reader.next());
        reader.seek(reader.getPointer() - 1);
        reader.skipSpaces();
        assertEquals('4', reader.next());
        reader.skipSpaces();
        assertEquals('s', reader.next());
        reader.next();
        reader.skipSpaces();
        assertFalse(reader.hasNext());
        close();
    }

    private void readOpeningTagName(String input, String output, char next) {
        recreate(input);
        assertEquals(output, reader.readOpeningTagName(true));
        assertEquals(next, reader.next());
        close();
    }

    @Test
    public void readOpeningTagName() {
        readOpeningTagName("TAG]", "TAG", ']');
        readOpeningTagName("TAG   ]", "TAG", ' ');
        readOpeningTagName("TAG iu ", "TAG", ' ');
        readOpeningTagName("TAG=", "TAG", '=');
        readOpeningTagName("TAG  =", "TAG", ' ');
        readOpeningTagName("TAG", null, 'T');
    }

    private void readClosingTag(String input, String expectedName, Character next) {
        recreate(input);
        assertTrue(reader.readClosingTag(new Tag(expectedName), true));
        if (next == null) {
            assertFalse(reader.hasNext());
        } else {
            assertEquals(next.charValue(), reader.next());
        }
        close();
    }

    private void readClosingTagNull(String input) {
        recreate(input);
        try {
            assertFalse(reader.readClosingTag(new Tag(input), true));
        } finally {
            close();
        }
    }

    @Test
    public void readClosingTag() {
        readClosingTag("[/TAG]", "TAG", null);
        readClosingTag("[/  TAG] ", "TAG", ' ');
        readClosingTag("[/ TAG ]", "TAG", null);
        readClosingTagNull("[/TAG= ]");
        readClosingTagNull("[/TAG\" ]");
        readClosingTagNull("[/TAG\' ]");
        readClosingTagNull("[/TAG[ ]");
        readClosingTagNull("[/ TAG  =]");
        readClosingTagNull("[/TAG");
        readClosingTagNull("[/ TAG iu ]");
    }

    private void readAttributeValue(String input, String output, char next) {
        recreate(input);
        assertEquals(output, reader.readAttrValue());
        assertEquals(next, reader.next());
        close();
    }

    private void readAttributeValueNull(String input) {
        recreate(input);
        try {
            assertNull(reader.readAttrValue());
        } finally {
            close();
        }
    }

    @Test
    public void readAttributeValue() {
        readAttributeValue("value ", "value", ' ');
        readAttributeValue("val]ue ", "val", ']');
        readAttributeValue("value]", "value", ']');
        readAttributeValue("\"value\"  ", "value", ' ');
        readAttributeValue("\'value\' ", "value", ' ');
        readAttributeValue("\"value[0]\"  ", "value[0]", ' ');
        readAttributeValue("\'value[0]\' ", "value[0]", ' ');
        readAttributeValue("\"value \"  ", "value ", ' ');
        readAttributeValue("\'value \' ", "value ", ' ');
        readAttributeValue("\" value \' \"]", " value \' ", ']');
        readAttributeValue("\' value \" \']", " value \" ", ']');
        readAttributeValue(" value", "", ' ');
        readAttributeValueNull("\"value\'");
        readAttributeValueNull("\'value\"");
        readAttributeValueNull("value");
        readAttributeValueNull("val\'ue");
        readAttributeValueNull("value\"");
    }

    private void readAttributeKey(String input, String output, char next) {
        recreate(input);
        assertEquals(output, reader.readAttrKey(true));
        assertEquals(next, reader.next());
        close();
    }

    private void readAttributeKeyNull(String input) {
        recreate(input);
        try {
            assertNull(reader.readAttrKey(true));
        } finally {
            close();
        }
    }

    @Test
    public void readAttributeKey() {
        readAttributeKey("key=", "key", '=');
        readAttributeKey("key =", "key", ' ');
        readAttributeKey("\'key\' =", "key", ' ');
        readAttributeKey("\"key\" =", "key", ' ');
        readAttributeKey("\'key[0]\' =", "key[0]", ' ');
        readAttributeKey("\"key[0]\" =", "key[0]", ' ');
        readAttributeKey("\' key \' =", " key ", ' ');
        readAttributeKey("\" key \"=", " key ", '=');
        readAttributeKey("\' \"key \' =", " \"key ", ' ');
        readAttributeKey("\" \'key \" =", " \'key ", ' ');
        readAttributeKeyNull("\"key\'");
        readAttributeKeyNull("\'key\"");
        readAttributeKeyNull("key");
        readAttributeKeyNull("k]ey");
    }

    private Map<String, String> mapOf(String... keyValuePairs) {
        Map<String, String> map = new HashMap<String, String>();
        if (keyValuePairs.length % 2 != 0) {
            fail();
        }
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            String key = keyValuePairs[i];
            String value = keyValuePairs[i + 1];
            map.put(key, value);
        }
        return map;
    }

    private void readAttributes(String input, Map<String, String> output) {
        recreate(input);
        Map<String, String> expected = reader.readAttributes(true);
        assertEquals(expected.size(), output.size());
        assertTrue(expected.entrySet().containsAll(output.entrySet()));
        assertEquals(']', reader.next());
    }

    private void readAttributesNull(String input) {
        recreate(input);
        try {
            assertNull(reader.readAttributes(true));
        } finally {
            close();
        }
    }

    @Test
    public void readAttributes() {
        readAttributes("key=value]", mapOf("key", "value"));
        readAttributes("key=val]ue]", mapOf("key", "val"));
        readAttributes("key = value ]", mapOf("key", "value"));
        readAttributes("key = \'value\' ]", mapOf("key", "value"));
        readAttributes("key = \"value\" ]", mapOf("key", "value"));
        readAttributes("\'key\' = value ]", mapOf("key", "value"));
        readAttributes("\"key\" = value ]", mapOf("key", "value"));
        readAttributes("\'key[0]\'=\"value[0]\"]", mapOf("key[0]", "value[0]"));
        readAttributesNull("key=value\']");
        readAttributesNull("key=value\"]");
        readAttributesNull("key = \"value\' ]");
        readAttributesNull("key = \"value ]");
        readAttributesNull("key = \'value]");
        readAttributesNull("key = \'val\'ue]");
        readAttributesNull("key = \"val\"ue]");

        readAttributes("k1=v1 k2=v2]", mapOf("k1", "v1", "k2", "v2"));
        readAttributes("k1=v1] k2=v2]", mapOf("k1", "v1"));
        readAttributes("\"k1\"=\'v1\' k2 = v2 k3=v3]", mapOf("k1", "v1", "k2", "v2", "k3", "v3"));
        readAttributes("k1=\'v1\' \'k2\'=v2]", mapOf("k1", "v1", "k2", "v2"));
        readAttributesNull("k1=v1\'k2\'=v2]");
        readAttributesNull("k1=v1 =v2]");
        readAttributesNull("=k1 = v1");
        readAttributesNull("k1==v1");
        readAttributesNull("k1=]v1");
        readAttributesNull("k1]=v1");
    }

    private void readOpeningTag(String input, String name, Map<String, String> attrs) {
        recreate(input);
        Tag tag = reader.readOpeningTag(true);
        Map<String, String> expectedAttrs = tag.getAttributes();
        assertEquals(name, tag.getName());
        assertTrue(expectedAttrs.entrySet().containsAll(attrs.entrySet()));
        assertTrue(attrs.entrySet().containsAll(expectedAttrs.entrySet()));
    }

    private void readOpeningTagNull(String input) {
        recreate(input);
        try {
            assertNull(reader.readOpeningTag(true));
        } finally {
            close();
        }
    }

    @Test
    public void readOpeningTag() {
        readOpeningTag("[TAG]", "TAG", mapOf());
        readOpeningTag("[ TAG]", "TAG", mapOf());
        readOpeningTag("[TAG key=value]", "TAG", mapOf("key", "value"));
        readOpeningTag("[TAG=value]", "TAG", mapOf("TAG", "value"));
        readOpeningTag("[TAG=v1 k2=v2]", "TAG", mapOf("TAG", "v1", "k2", "v2"));
        readOpeningTagNull("[TAG");
        readOpeningTagNull("[TAG=2 key]");
        readOpeningTagNull("[TAG value]");
    }
}
