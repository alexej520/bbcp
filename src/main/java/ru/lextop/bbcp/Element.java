package ru.lextop.bbcp;

import java.util.List;

public class Element {
    private final Tag mTag;
    private final String mText;
    private final List<Element> mChilds;

    private Element(String text, Tag tag, List<Element> childs) {
        mText = text;
        mTag = tag;
        mChilds = childs;
    }

    public Element(Tag tag, List<Element> childs) {
        this(null, tag, childs);
    }

    public Element(String text) {
        this(text, null, null);
    }

    public boolean isText() {
        return mText != null;
    }

    public boolean isTag() {
        return mTag != null;
    }

    public boolean isRoot() {
        return mText == null && mTag == null;
    }

    public String getText() {
        return mText;
    }

    public Tag getTag() {
        return mTag;
    }

    public List<Element> getChilds() {
        return mChilds;
    }

    @Override
    public String toString() {
        if (isText()) {
            return "Text(" + mText + ")";
        } else if (isTag()) {
            return "Tag(" + mTag.toString() + ")";
        } else {
            return super.toString();
        }
    }
}
