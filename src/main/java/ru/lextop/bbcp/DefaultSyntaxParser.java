package ru.lextop.bbcp;

import java.util.ArrayList;
import java.util.List;

public final class DefaultSyntaxParser implements SyntaxParser {
    public Element read(Tag tag, BbReader reader, BbContext context) {
        int backup = reader.getPointer();
        int first = backup + 1;
        int last = first;
        boolean caseSensitive = context.isCaseSensitive();
        List<Element> childs = new ArrayList<Element>();

        while (reader.hasNext()) {
            last = reader.getPointer();
            if (tag != null && reader.readClosingTag(tag, caseSensitive)) {
                String lastPlain = reader.buildString(first, last, true);
                if (lastPlain.length() > 0) {
                    childs.add(new Element(lastPlain));
                }
                return new Element(tag, childs);
            } else {
                Tag innerTag = reader.readOpeningTag(caseSensitive);
                if (innerTag != null) {

                    Element child;
                    try {
                        child = context.readElement(innerTag, reader);
                    } catch (SyntaxParserNotFoundException e) {
                        child = null;
                    }
                    if (child != null) {
                        String preChildPlain = reader.buildString(first, last, true);
                        first = reader.getPointer() + 1;
                        if (preChildPlain.length() > 0) {
                            childs.add(new Element(preChildPlain));
                        }
                        childs.add(child);
                    } else {
                        reader.next();
                    }
                } else {
                    reader.next();
                }
            }
        }
        if (tag == null) {
            if (first <= reader.getPointer()) {
                String lastPlain = reader.buildString(first, last + 1, true);
                if (lastPlain.length() > 0) {
                    childs.add(new Element(lastPlain));
                }
            }
            return new Document(childs);
        } else {
            reader.seek(backup);
            return null;
        }
    }
}
