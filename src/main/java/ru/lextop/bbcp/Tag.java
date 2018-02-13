package ru.lextop.bbcp;

import java.util.Collections;
import java.util.Map;

public final class Tag {
    private final String mName;
    private final Map<String, String> mAttrs;

    public Tag(String name, Map<String, String> attrs) {
        mName = name;
        mAttrs = attrs;
    }

    public Tag(String name) {
        this(name, Collections.<String, String>emptyMap());
    }

    public String getName() {
        return mName;
    }

    public Map<String, String> getAttributes() {
        return mAttrs;
    }

    @Override
    public String toString() {
        return mName + ": " + mAttrs.toString();
    }
}
