package ru.lextop.bbcp;

import java.util.List;

public final class Document extends Element {
    public Document(List<Element> childs) {
        super(null, childs);
    }

    @Override
    public String toString() {
        if (isRoot()) {
            return "Document()";
        } else {
            return super.toString();
        }
    }
}
