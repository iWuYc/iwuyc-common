/*
 * 深圳市灵智数科有限公司版权所有.
 */
package com.iwuyc.tools.commons.basic.collections;

import lombok.Data;
import lombok.ToString;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;
import java.util.Map;

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
