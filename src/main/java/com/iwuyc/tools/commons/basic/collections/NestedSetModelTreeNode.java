/*
 * 深圳市灵智数科有限公司版权所有.
 */
package com.iwuyc.tools.commons.basic.collections;

import lombok.Data;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 功能说明
 *
 * @author 吴宇春
 * @version 1.0.0
 * @date 2022/6/13
 */
@Data
public class NestedSetModelTreeNode<T> implements Comparable<NestedSetModelTreeNode<T>> {
    private final String nodeName;
    /**
     * 当前节点的右分数
     */
    int rightScore;
    /**
     * 兄弟节点中的排序值，升序，越小越靠前
     */
    int sort;
    private NestedSetModelTreeNode<T> parentNode;
    private Map<String, NestedSetModelTreeNode<T>> children = new LinkedHashMap<>();
    /**
     * 当前节点的左分数
     */
    private int leftScore;
    private T data;

    public void scoreCalc(AtomicInteger counter, int stepLength) {
        final int leftScore = counter.getAndAdd(stepLength);
        final int rightScore;
        if (this.children.size() != 0) {
            final List<NestedSetModelTreeNode<T>> treeNodes = new ArrayList<>(children.values());
            Collections.sort(treeNodes);
            for (NestedSetModelTreeNode<T> item : treeNodes) {
                item.scoreCalc(counter, stepLength);
            }
        }
        rightScore = counter.getAndAdd(stepLength);
        this.leftScore = leftScore;
        this.rightScore = rightScore;
    }

    public NestedSetModelTreeNode(String nodeName, NestedSetModelTreeNode<T> parentNode) {
        this.nodeName = nodeName;
        this.parentNode = parentNode;
    }

    @Override
    public int compareTo(@Nonnull NestedSetModelTreeNode<T> that) {
        return this.getSort() <= that.getSort() ? -1 : 1;
    }

    public void changeParent(NestedSetModelTreeNode<T> newParentNode) {
        // TODO Neil 待完善
    }

    @Override
    public String toString() {
        return "NestedSetModelTreeNode{" +
                "nodeName='" + nodeName + '\'' +
                ", rightScore=" + rightScore +
                ", sort=" + sort +
                ", leftScore=" + leftScore +
                ", data=" + data +
                '}';
    }
}
