package ru.lextop.bbcp;

import java.util.Map;

public final class BbContext {
    private final boolean mCaseSensitive;
    private final Map<String, SyntaxParser> mSyntaxParsers;
    private final SyntaxParser mDefaultSyntaxParser;

    BbContext(boolean caseSensitive, Map<String, SyntaxParser> syntaxParsers, SyntaxParser defaultSyntaxParser) {
        mCaseSensitive = caseSensitive;
        mSyntaxParsers = syntaxParsers;
        mDefaultSyntaxParser = defaultSyntaxParser;
    }

    public boolean isCaseSensitive() {
        return mCaseSensitive;
    }

    public Element readElement(Tag tag, BbReader input) {
        String caseTag;
        if (tag == null) {
            caseTag = null;
        } else if (mCaseSensitive) {
            caseTag = tag.getName();
        } else {
            caseTag = tag.getName().toLowerCase();
        }
        SyntaxParser parser = mSyntaxParsers.get(caseTag);
        if (parser == null) parser = mDefaultSyntaxParser;
        if (parser == null) throw new SyntaxParserNotFoundException(caseTag);
        return parser.read(tag, input, this);
    }
}
