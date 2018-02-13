package ru.lextop.bbcp;

import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class DefaultSyntaxParserTest {
    private BbReader reader;

    private void recreate(String input) {
        reader = new BbReader(input);
    }

    private void close() {
        try {
            reader.close();
        } catch (IOException e) {
            fail();
        }
    }

    @Test
    public void readRoot() {
        SyntaxParser adapter = new DefaultSyntaxParser();
        recreate(" text[/TAG]");
        Element element = adapter.read(null, reader, new BbContext(true, new HashMap<String, SyntaxParser>(), null));
        assertTrue(element.isRoot());
        assertEquals(" text[/TAG]", element.getChilds().get(0).getText());
        close();
    }

    @Test
    public void readSimple() {
        SyntaxParser adapter = new DefaultSyntaxParser();
        recreate(" text[/TAG]");
        Element element = adapter.read(new Tag("TAG"), reader, new BbContext(true, new HashMap<String, SyntaxParser>(), null));
        assertTrue(element.isTag());
        assertEquals("TAG", element.getTag().getName());
        assertTrue(element.getChilds().get(0).isText());
        assertEquals(" text", element.getChilds().get(0).getText());
        close();
    }

    @Test
    public void readNestedSameTag() {
        SyntaxParser adapter = new DefaultSyntaxParser();
        recreate(" t[TAG]e[/TAG]xt[/TAG]");
        Map<String, SyntaxParser> adapters = new HashMap<String, SyntaxParser>();
        adapters.put("TAG", new DefaultSyntaxParser());
        Element element = adapter.read(new Tag("TAG"), reader, new BbContext(true, adapters, null));
        assertTrue(element.isTag());
        assertEquals("TAG", element.getTag().getName());
        assertTrue(element.getChilds().get(0).isText());
        assertEquals(" t", element.getChilds().get(0).getText());
        assertTrue(element.getChilds().get(1).isTag());
        assertEquals("TAG", element.getChilds().get(1).getTag().getName());
        assertTrue("e", element.getChilds().get(1).getChilds().get(0).isText());
        assertEquals("e", element.getChilds().get(1).getChilds().get(0).getText());
        assertTrue(element.getChilds().get(2).isText());
        assertEquals("xt", element.getChilds().get(2).getText());
        close();
    }

    @Test
    public void readNestedTag() {
        SyntaxParser adapter = new DefaultSyntaxParser();
        recreate(" t[B]e[/B]xt[/TAG]");
        Map<String, SyntaxParser> adapters = new HashMap<String, SyntaxParser>();
        adapters.put("TAG", new DefaultSyntaxParser());
        adapters.put("B", new DefaultSyntaxParser());
        Element element = adapter.read(new Tag("TAG"), reader, new BbContext(true, adapters, null));
        assertTrue(element.isTag());
        assertEquals("TAG", element.getTag().getName());
        assertTrue(element.getChilds().get(0).isText());
        assertEquals(" t", element.getChilds().get(0).getText());
        assertTrue(element.getChilds().get(1).isTag());
        assertEquals("B", element.getChilds().get(1).getTag().getName());
        assertTrue("e", element.getChilds().get(1).getChilds().get(0).isText());
        assertEquals("e", element.getChilds().get(1).getChilds().get(0).getText());
        assertTrue(element.getChilds().get(2).isText());
        assertEquals("xt", element.getChilds().get(2).getText());
        close();
    }

    @Test
    public void readNestedTagWithAttrs() {
        SyntaxParser adapter = new DefaultSyntaxParser();
        recreate(" t[B key=value]e[/B]xt[/TAG]");
        Map<String, SyntaxParser> adapters = new HashMap<String, SyntaxParser>();
        adapters.put("TAG", new DefaultSyntaxParser());
        adapters.put("B", new DefaultSyntaxParser());
        Element element = adapter.read(new Tag("TAG"), reader, new BbContext(true, adapters, null));
        assertTrue(element.isTag());
        assertEquals("TAG", element.getTag().getName());
        assertTrue(element.getChilds().get(0).isText());
        assertEquals(" t", element.getChilds().get(0).getText());
        assertTrue(element.getChilds().get(1).isTag());
        assertEquals("B", element.getChilds().get(1).getTag().getName());
        Map<String, String> attrs = element.getChilds().get(1).getTag().getAttributes();
        assertEquals(1, attrs.size());
        assertEquals("value", attrs.get("key"));
        assertTrue("e", element.getChilds().get(1).getChilds().get(0).isText());
        assertEquals("e", element.getChilds().get(1).getChilds().get(0).getText());
        assertTrue(element.getChilds().get(2).isText());
        assertEquals("xt", element.getChilds().get(2).getText());
        close();
    }
}
