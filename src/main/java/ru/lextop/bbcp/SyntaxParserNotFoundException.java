package ru.lextop.bbcp;

public final class SyntaxParserNotFoundException extends RuntimeException {
    public SyntaxParserNotFoundException() {
    }

    public SyntaxParserNotFoundException(String s) {
        super(s);
    }

    public SyntaxParserNotFoundException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public SyntaxParserNotFoundException(Throwable throwable) {
        super(throwable);
    }
}
