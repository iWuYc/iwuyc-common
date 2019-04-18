package com.iwuyc.tools.commons.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * @author Neil
 */
public class PlaceholderExtract {
    private final String source;
    private final Stack<Integer> stack = new Stack<>();
    private int position;
    private int matchCount = 0;

    public PlaceholderExtract(String sourceStr) {
        this.source = sourceStr;
    }

    public List<String> compile(String startStr, String endStr) {
        int indexStart;
        int indexEnd;
        do {
            indexStart = this.source.indexOf(startStr, position);
            if (indexStart < 0) {
                break;
            }
            push(indexStart);
            position = indexStart + 2;
            indexEnd = this.source.indexOf(endStr, position);
            if (indexEnd >= 0) {
                push(indexEnd);
            }

        } while (true);
        return buil();
    }

    private List<String> buil() {
        if (this.stack.isEmpty()) {
            return Collections.emptyList();
        }
        int indexStart = -1;
        int indexEnd = -1;
        List<String> placeholders = new ArrayList<>();
        while (!this.stack.isEmpty()) {
            int temp = this.stack.pop();
            char ch = this.source.charAt(temp);
            switch (ch) {
                case '}':
                    matchCount++;
                    if (indexEnd < 0) {
                        indexEnd = temp;
                    }
                    break;
                case '#':
                    matchCount--;
                    indexStart = temp;
                    break;
                default:
                    break;
            }
            if (matchCount == 0) {
                String placeholder = this.source.substring(indexStart, indexEnd + 2);
                placeholders.add(placeholder);
                indexStart = -1;
                indexEnd = -1;
            }
        }

        if (indexStart >= 0 || indexEnd >= 0) {
            throw new IllegalArgumentException("startStr跟endStr不匹配。");
        }
        return placeholders;
    }

    private void push(int index) {
        if (this.stack.isEmpty()) {
            this.stack.push(index);
            return;
        }
        if (this.stack.peek() > index) {
            Integer tmpIndex = this.stack.pop();
            this.stack.push(index);
            this.stack.push(tmpIndex);
            return;
        }

        this.stack.push(index);
    }
}
