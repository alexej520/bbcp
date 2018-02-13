package ru.lextop.bbcp;

import java.io.IOException;
import java.util.*;

public final class Bb {
    private final BbContext mContext;

    private Bb(BbContext context) {
        mContext = context;
    }

    public Document parse(String text) {
        BbReader reader = new BbReader(text);
        Document document = (Document) mContext.readElement(null, reader);
        try {
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException();
        }
        return document;
    }

    public static Builder builder(boolean caseSensitive) {
        return new Builder(caseSensitive);
    }

    public static class Builder {
        private final boolean mCaseSensitive;
        private final Map<String, SyntaxParser> mSyntaxParsers;
        private SyntaxParser mDefaultSyntaxParser;

        private Builder(boolean caseSensitive) {
            mCaseSensitive = caseSensitive;
            mSyntaxParsers = new HashMap<String, SyntaxParser>();
        }

        public Builder register(SyntaxParser syntaxParser, String... tagNames) {
            for (String name : tagNames) {
                String caseName;
                if (name == null) {
                    caseName = null;
                } else if (mCaseSensitive) {
                    caseName = name;
                } else {
                    caseName = name.toLowerCase();
                }
                mSyntaxParsers.put(caseName, syntaxParser);
            }
            return this;
        }

        public Builder registerDefault(SyntaxParser parser) {
            mDefaultSyntaxParser = parser;
            return this;
        }

        public Bb build() {
            return new Bb(new BbContext(mCaseSensitive, mSyntaxParsers, mDefaultSyntaxParser));
        }
    }
}
