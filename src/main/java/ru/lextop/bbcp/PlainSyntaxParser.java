package ru.lextop.bbcp;

import java.util.Collections;

public final class PlainSyntaxParser implements SyntaxParser {
    public Element read(Tag tag, BbReader reader, BbContext context) {
        int backup = reader.getPointer();
        int first = backup + 1;
        int last = first;
        boolean caseSensitive = context.isCaseSensitive();
        while (reader.hasNext()) {
            last = reader.getPointer();
            if (tag != null && reader.readClosingTag(tag, caseSensitive)) {
                String plain = reader.buildString(first, last, true);
                return new Element(tag, Collections.singletonList(new Element(plain)));
            } else {
                reader.next();
            }
        }
        if (tag == null) {
            String plain = reader.buildString(first, last + 1, true);
            return new Document(Collections.singletonList(new Element(plain)));
        } else {
            reader.seek(backup);
            return null;
        }
    }
}
