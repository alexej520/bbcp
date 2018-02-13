package ru.lextop.bbcp;

public interface SyntaxParser {
    Element read(Tag tag, BbReader input, BbContext context);
}
