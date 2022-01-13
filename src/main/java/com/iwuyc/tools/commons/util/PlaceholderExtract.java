package com.iwuyc.tools.commons.util;

import com.iwuyc.tools.commons.basic.type.PairTuple;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @author Neil
 */
@Slf4j
public class PlaceholderExtract {
    private final String source;
    private final Stack<Integer> stack = new Stack<>();
    private int position;

    public PlaceholderExtract(String sourceStr) {
        this.source = sourceStr;
    }

    public List<String> compile(String startStr, String endStr) {
        int indexStart;
        int indexEnd = this.source.indexOf(endStr);
        final List<PairTuple<Integer, Integer>> startAndEndPairList = new ArrayList<>();
        do {

            indexStart = this.source.indexOf(startStr, position);
            position = indexStart + startStr.length();
            if (indexStart >= 0) {
                this.stack.push(indexStart);
            }

            if (indexStart > indexEnd) {
                final Integer startIndexOverEndIndex = this.stack.pop();
                startAndEndPairList.addAll(buildIndexPair(endStr, indexEnd));
                this.stack.push(startIndexOverEndIndex);
                indexEnd = this.source.indexOf(endStr, position);
                position = indexStart + endStr.length();
            } else if (indexStart < 0) {
                startAndEndPairList.addAll(buildIndexPair(endStr, indexEnd));
            }

            if (indexEnd < 0 || indexStart < 0 || position >= this.source.length()) {
                this.stack.clear();
                break;
            }
        } while (true);
        return buildIndexPair(startAndEndPairList, endStr.length());
    }

    private List<PairTuple<Integer, Integer>> buildIndexPair(String endStr, int indexEnd) {
        List<PairTuple<Integer, Integer>> result = new ArrayList<>();
        int endPosition = indexEnd;
        while (!stack.isEmpty()) {
            final Integer lastStartIndex = stack.pop();
            final int endIndex = this.source.indexOf(endStr, endPosition);
            if (endIndex < 0) {
                log.info("已无为之相匹配的结束符。endStr:{};Position:{}", endStr, endPosition);
                continue;
            }
            PairTuple<Integer, Integer> startAndEnd = new PairTuple<>(lastStartIndex, endIndex);
            result.add(startAndEnd);
            endPosition += endStr.length();
        }
        return result;
    }

    private List<String> buildIndexPair(final List<PairTuple<Integer, Integer>> startAndEndPairList, int endStrLength) {
        List<String> result = new ArrayList<>(startAndEndPairList.size());
        for (PairTuple<Integer, Integer> item : startAndEndPairList) {
            final Integer startIndex = item.getKey();
            final Integer endIndex = item.getVal();
            final String placeHolder = this.source.substring(startIndex, endIndex + endStrLength);
            result.add(placeHolder);
        }
        return result;
    }

}
