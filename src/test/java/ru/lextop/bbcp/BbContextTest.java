package ru.lextop.bbcp;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class BbContextTest {
    @Test
    public void elementAdapterNotFound() {
        Map<String, SyntaxParser> adapters = new HashMap<String, SyntaxParser>();
        adapters.put("TAG", Mockito.mock(SyntaxParser.class));
        adapters.put("B", Mockito.mock(SyntaxParser.class));
        BbContext context = new BbContext(true, adapters, null);
        try {
            context.readElement(new Tag("TAG"), new BbReader(""));
        } catch (SyntaxParserNotFoundException e) {
            fail();
        } catch (Throwable t) {
        }
        try {
            context.readElement(new Tag("UNKNOWN"), new BbReader(""));
        } catch (SyntaxParserNotFoundException e) {
        } catch (Throwable t) {
            fail();
        }
    }
}
