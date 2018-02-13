package ru.lextop.bbcp;

import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;

import static org.junit.Assert.*;

public class PlainSyntaxParserTest {
    private BbReader reader;
    private PlainSyntaxParser adapter;

    private void recreate(String input) {
        reader = new BbReader(input);
        adapter = new PlainSyntaxParser();
    }

    private void close() {
        try {
            reader.close();
        } catch (IOException e) {
            fail();
        }
    }

    @Test
    public void readSimple() {
        recreate(" text[/PLAIN]");
        Element element = adapter.read(new Tag("PLAIN"), reader, new BbContext(true, new HashMap<String, SyntaxParser>(), null));
        assertTrue(element.isTag());
        assertEquals("PLAIN", element.getTag().getName());
        assertTrue(element.getChilds().get(0).isText());
        assertEquals(" text", element.getChilds().get(0).getText());
        close();
    }

    @Test
    public void readWithInnerTags() {
        recreate("t[B]ex[/B]t[/PLAIN]");
        Element element = adapter.read(new Tag("PLAIN"), reader, new BbContext(true, new HashMap<String, SyntaxParser>(), null));
        assertTrue(element.isTag());
        assertEquals("PLAIN", element.getTag().getName());
        assertTrue(element.getChilds().get(0).isText());
        assertEquals("t[B]ex[/B]t", element.getChilds().get(0).getText());
        close();
    }

    @Test
    public void readRoot() {
        recreate("t[B]ex[/B]t[/PLAIN]");
        Element element = adapter.read(null, reader, new BbContext(true, new HashMap<String, SyntaxParser>(), null));
        assertTrue(element.isRoot());
        assertTrue(element.getChilds().get(0).isText());
        assertEquals("t[B]ex[/B]t[/PLAIN]", element.getChilds().get(0).getText());
        close();
    }
}
