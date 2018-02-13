package ru.lextop.bbcp;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class BbReader implements Closeable {

    public static final char TAG_START = '[';
    public static final char CLOSE_TAG_START = '/';
    public static final char TAG_END = ']';
    public static final char EQUALS = '=';
    public static final char QUOTE = '\"';
    public static final char APOSTROPHE = '\'';
    public static final char SPACE = ' ';

    private final String mInput;
    private final int mInputLast;
    private int mPointer;

    public int getPointer() {
        return mPointer;
    }

    public void seek(int pos) {
        mPointer = pos;
    }

    public BbReader(String input) {
        mInput = input;
        mInputLast = mInput.length() - 1;
        mPointer = -1;
    }

    public boolean hasNext() {
        return mPointer < mInputLast;
    }

    public char next() {
        mPointer++;
        return mInput.charAt(mPointer);
    }

    public void close() throws IOException {

    }

    public String buildString(int first, int last, boolean caseSensitive) {
        String string = mInput.substring(first, last + 1);
        return caseSensitive ? string : string.toLowerCase();
    }


    public boolean readClosingTag(Tag openingTag, boolean caseSensitive) {
        String openingTagName = openingTag.getName();
        int backup = getPointer();
        int symbolPos = 0;
        int len = openingTagName.length();
        if (!((hasNext() && next() == TAG_START) && (hasNext() && next() == CLOSE_TAG_START))) {
            seek(backup);
            return false;
        }
        skipSpaces();
        while (hasNext()) {
            char symbol = next();
            if (symbol == TAG_END) {
                if (symbolPos == len) {
                    return true;
                } else {
                    seek(backup);
                    return false;
                }
            } else if (symbol == SPACE) {
                if (symbolPos == len) {
                    skipSpaces();
                    if (hasNext() && next() == TAG_END) {
                        return true;
                    } else {
                        seek(backup);
                        return false;
                    }
                } else {
                    seek(backup);
                    return false;
                }

            } else if (symbol == TAG_START || symbol == EQUALS || symbol == APOSTROPHE || symbol == QUOTE) {
                seek(backup);
                return false;
            } else if (caseSensitive ?
                    symbol == openingTagName.charAt(symbolPos) :
                    Character.toLowerCase(symbol) == Character.toLowerCase(openingTagName.charAt(symbolPos))) {
                symbolPos++;
            } else {
                seek(backup);
                return false;
            }
        }
        seek(backup);
        return false;
    }

    public void skipSpaces() {
        while (hasNext()) {
            if (next() != SPACE) {
                seek(getPointer() - 1);
                return;
            }
        }
    }

    public Tag readOpeningTag(boolean caseSensitive) {
        int backup = getPointer();
        if (!(hasNext() && next() == TAG_START)) {
            seek(backup);
            return null;
        }
        String name;
        skipSpaces();
        int backupName = getPointer();
        name = readOpeningTagName(caseSensitive);
        if (name == null) {
            seek(backup);
            return null;
        }
        skipSpaces();
        if (hasNext()) {
            char symbol = next();
            if (symbol == EQUALS) {
                seek(backupName);
                Map<String, String> attrs = readAttributes(caseSensitive);
                if (attrs == null) {
                    seek(backup);
                    return null;
                }
                skipSpaces();
                if (hasNext() && next() == TAG_END) {
                    return new Tag(name, attrs);
                } else {
                    seek(backup);
                    return null;
                }
            } else if (symbol == TAG_END) {
                return new Tag(name);
            } else {
                seek(getPointer() - 1);
                Map<String, String> attrs = readAttributes(caseSensitive);
                if (attrs == null) {
                    seek(backup);
                    return null;
                }
                skipSpaces();
                if (hasNext() && next() == TAG_END) {
                    return new Tag(name, attrs);
                } else {
                    seek(backup);
                    return null;
                }
            }
        }
        seek(backup);
        return null;
    }

    public String readOpeningTagName(boolean caseSensitive) {
        int backup = getPointer();
        int first = getPointer() + 1;
        while (hasNext()) {
            char symbol = next();
            if (symbol == TAG_END || symbol == EQUALS || symbol == SPACE) {
                seek(getPointer() - 1);
                return buildString(first, getPointer(), caseSensitive);
            } else if (symbol == TAG_START || symbol == CLOSE_TAG_START) {
                seek(backup);
                return null;
            }
        }
        seek(backup);
        return null;
    }

    public Map<String, String> readAttributes(boolean caseSensitive) {
        int backup = getPointer();
        Map<String, String> attrs = new HashMap<String, String>();
        String attrKey = null;
        while (hasNext()) {
            if (attrKey == null) {
                attrKey = readAttrKey(caseSensitive);
                if (attrKey == null) return null;
                skipSpaces();
            } else {
                if (hasNext()) {
                    if (next() == EQUALS) {
                        skipSpaces();
                        String value = readAttrValue();
                        if (value == null) return null;
                        attrs.put(attrKey, value);
                        attrKey = null;
                        if (hasNext()) {
                            char nextSymbol = next();
                            seek(getPointer() - 1);
                            if (nextSymbol == TAG_END || nextSymbol == SPACE) {
                            } else {
                                seek(backup);
                                return null;
                            }
                        } else {
                            seek(backup);
                            return null;
                        }
                        skipSpaces();
                        if (hasNext()) {
                            char nextSymbol = next();
                            seek(getPointer() - 1);
                            if (nextSymbol == TAG_END) break;
                        } else {
                            seek(backup);
                            return null;
                        }
                    } else {
                        seek(backup);
                        return null;
                    }
                }
            }
        }
        if (attrKey != null) {
            seek(backup);
            return null;
        } else {
            return attrs;
        }
    }

    public String readAttrKey(boolean caseSensitive) {
        int backup = getPointer();
        char stop = next();
        if (stop == TAG_END || stop == EQUALS || stop == TAG_START) {
            seek(backup);
            return null;
        } else if (stop != QUOTE && stop != APOSTROPHE) {
            seek(getPointer() - 1);
            stop = SPACE;
        }
        int first = getPointer() + 1;
        if (stop != SPACE) {
            while (hasNext()) {
                char symbol = next();
                if (symbol == stop) {
                    return buildString(first, getPointer() - 1, caseSensitive);
                }
            }
            seek(backup);
            return null;
        } else {
            while (hasNext()) {
                char symbol = next();
                if (symbol == SPACE || symbol == EQUALS) {
                    seek(getPointer() - 1);
                    return buildString(first, getPointer(), caseSensitive);
                } else if (symbol == TAG_END || symbol == APOSTROPHE || symbol == QUOTE) {
                    seek(backup);
                    return null;
                }
            }
            seek(backup);
            return null;
        }
    }

    public String readAttrValue() {
        int backup = getPointer();
        char stop = next();
        if (stop == TAG_END || stop == EQUALS || stop == TAG_START) {
            seek(backup);
            return null;
        } else if (stop != QUOTE && stop != APOSTROPHE) {
            seek(getPointer() - 1);
            stop = SPACE;
        }
        int first = getPointer() + 1;
        if (stop != SPACE) {
            while (hasNext()) {
                char symbol = next();
                if (symbol == stop) {
                    return buildString(first, getPointer() - 1, true);
                }
            }
            seek(backup);
            return null;
        } else {
            boolean openBracket = false;
            while (hasNext()) {
                char symbol = next();
                if (openBracket) {
                    if (symbol == ')') {
                        openBracket = false;
                    } else if (symbol == TAG_END || symbol == EQUALS || symbol == APOSTROPHE || symbol == QUOTE) {
                        seek(backup);
                        return null;
                    }
                } else {
                    if (symbol == '(') {
                        openBracket = true;
                    } else if (symbol == SPACE || symbol == TAG_END) {
                        seek(getPointer() - 1);
                        return buildString(first, getPointer(), true);
                    } else if (symbol == EQUALS || symbol == APOSTROPHE || symbol == QUOTE) {
                        seek(backup);
                        return null;
                    }
                }
            }
            seek(backup);
            return null;
        }
    }
}
