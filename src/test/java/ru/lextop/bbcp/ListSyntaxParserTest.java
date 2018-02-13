package ru.lextop.bbcp;

import org.junit.Test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashMap;

import static org.junit.Assert.fail;

public class ListSyntaxParserTest {
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
    public void testListTag() {
        SyntaxParser adapter = new ListSyntaxParser("*");
        recreate("[*]item1[*]item2[/LIST]");
        Element element = adapter.read(new Tag("LIST"), reader, new BbContext(true, new HashMap<String, SyntaxParser>(), null));
        assertEquals(2, element.getChilds().size());
        assertTrue(element.getChilds().get(0).isTag());
        assertEquals("*", element.getChilds().get(0).getTag().getName());
        assertTrue(element.getChilds().get(1).isTag());
        assertEquals("*", element.getChilds().get(1).getTag().getName());
        close();
    }
}
