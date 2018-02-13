package ru.lextop.bbcp;

import java.util.ArrayList;
import java.util.List;

public final class ListSyntaxParser implements SyntaxParser {
    private String mListItemTagName;

    public ListSyntaxParser(String listItemTagName) {
        mListItemTagName = listItemTagName;
    }

    public Element read(Tag tag, BbReader reader, BbContext context) {
        int backup = reader.getPointer();
        int first = backup + 1;
        int last = first;
        boolean caseSensitive = context.isCaseSensitive();
        List<Element> items = new ArrayList<Element>();

        while (reader.hasNext()) {
            last = reader.getPointer();
            if (tag != null && reader.readClosingTag(tag, caseSensitive)) {
                String lastPlain = reader.buildString(first, last, true);
                if (lastPlain.length() > 0) {
                    items.add(new Element(lastPlain));
                }
                return new Element(tag, items);
            } else {
                Tag innerTag = reader.readOpeningTag(caseSensitive);
                if (innerTag != null) {
                    Element child;
                    try {
                        if (mListItemTagName.equals(innerTag.getName())) {
                            child = readItem(tag, innerTag, reader, context);
                        } else {
                            child = context.readElement(innerTag, reader);
                        }
                    } catch (SyntaxParserNotFoundException e) {
                        child = null;
                    }
                    if (child != null) {
                        String preChildPlain = reader.buildString(first, last, true);
                        first = reader.getPointer() + 1;
                        if (preChildPlain.length() > 0) {
                            items.add(new Element(preChildPlain));
                        }
                        items.add(child);
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
                    items.add(new Element(lastPlain));
                }
            }
            return new Document(items);
        } else {
            reader.seek(backup);
            return null;
        }
    }

    private Element readItem(Tag listTag, Tag itemTag, BbReader reader, BbContext context) {
        int backup = reader.getPointer();
        int first = backup + 1;
        int last = first;
        boolean caseSensitive = context.isCaseSensitive();
        List<Element> childs = new ArrayList<Element>();

        while (reader.hasNext()) {
            last = reader.getPointer();
            if (listTag != null && reader.readClosingTag(listTag, caseSensitive)) {
                String lastPlain = reader.buildString(first, last, true);
                if (lastPlain.length() > 0) {
                    childs.add(new Element(lastPlain));
                }
                reader.seek(last);
                return new Element(itemTag, childs);
            } else {
                Tag innerTag = reader.readOpeningTag(caseSensitive);
                if (innerTag != null) {
                    if (innerTag.getName().equals(mListItemTagName)) {
                        reader.seek(last);
                        String lastPlain = reader.buildString(first, last, true);
                        if (lastPlain.length() > 0) {
                            childs.add(new Element(lastPlain));
                        }
                        return new Element(itemTag, childs);
                    }

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
        if (listTag == null) {
            if (first < reader.getPointer()) {
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
