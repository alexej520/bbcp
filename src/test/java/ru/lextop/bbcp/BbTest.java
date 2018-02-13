package ru.lextop.bbcp;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class BbTest {
    @Test
    public void nestedPlainTextTest() {
        Bb bb = Bb.builder(true)
                .register(new DefaultSyntaxParser(), null, "TAG", "B")
                .register(new PlainSyntaxParser(), "PLAIN")
                .build();
        Document document = bb.parse("preTag[TAG=v1 k2=v2]prePlain[PLAIN key = value][B]plain[/B][/PLAIN][/TAG]postTag");
        {
            assertTrue(document.isRoot());
            assertNull(document.getText());
            assertNull(document.getTag());
            assertEquals(3, document.getChilds().size());
            assertTrue(document.getChilds().get(0).isText());
            assertEquals("preTag", document.getChilds().get(0).getText());
            assertTrue(document.getChilds().get(1).isTag());
            assertEquals("TAG", document.getChilds().get(1).getTag().getName());
            Map<String, String> attrs = document.getChilds().get(1).getTag().getAttributes();
            assertEquals(2, attrs.size());
            assertEquals("v1", attrs.get("TAG"));
            assertEquals("v2", attrs.get("k2"));
            {
                assertEquals(2, document.getChilds().get(1).getChilds().size());
                assertTrue(document.getChilds().get(1).getChilds().get(0).isText());
                assertEquals("prePlain", document.getChilds().get(1).getChilds().get(0).getText());
                assertTrue(document.getChilds().get(1).getChilds().get(1).isTag());
                assertEquals("PLAIN", document.getChilds().get(1).getChilds().get(1).getTag().getName());
                attrs = document.getChilds().get(1).getChilds().get(1).getTag().getAttributes();
                assertEquals(1, attrs.size());
                assertEquals("value", attrs.get("key"));
                {
                    assertEquals(1, document.getChilds().get(1).getChilds().get(1).getChilds().size());
                    assertTrue(document.getChilds().get(1).getChilds().get(1).getChilds().get(0).isText());
                    assertEquals("[B]plain[/B]", document.getChilds().get(1).getChilds().get(1).getChilds().get(0).getText());
                }
            }
            assertTrue(document.getChilds().get(2).isText());
            assertEquals("postTag", document.getChilds().get(2).getText());
        }
    }
}
