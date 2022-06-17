/*
 * 深圳市灵智数科有限公司版权所有.
 */
package com.iwuyc.tools.commons.basic.collections;

import com.iwuyc.tools.commons.util.collection.ArrayUtil;
import com.iwuyc.tools.commons.util.collection.MapUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 功能说明
 *
 * @author 吴宇春
 * @version 1.0.0
 * @date 2022/6/13
 */
public class NestedSetModelTree<T> {
    NestedSetModelTree() {
        
    }

    public static <T> Builder<T> newBuilder() {
        return new Builder<>();
    }

    private NestedSetModelTreeNode<T> rootNode;
    @Setter
    @Getter
    private boolean autoCreateNode = true;

    public void addNode(T data, String... nodePaths) {
        this.addNode(data, 0, nodePaths);
    }

    public void addNode(final T data, final int sort, String... nodePaths) {
        final Optional<NestedSetModelTreeNode<T>> childNodeOpt = getNode(nodePaths);
        childNodeOpt.ifPresent(childNode -> {
            childNode.setData(data);
            childNode.setSort(sort);
        });
    }

    public Optional<NestedSetModelTreeNode<T>> getNode(String... nodePaths) {
        return getNode(this.autoCreateNode, nodePaths);
    }

    public Optional<NestedSetModelTreeNode<T>> getNode(boolean autoCreateNode, String... nodePaths) {
        if (ArrayUtil.isEmpty(nodePaths)) {
            return Optional.empty();
        }
        final String rootName = nodePaths[0];
        if (rootNode == null) {
            this.rootNode = new NestedSetModelTreeNode<>(rootName, null);
        }
        if (!Objects.equals(this.rootNode.getNodeName(), rootName)) {
            return Optional.empty();
        }
        NestedSetModelTreeNode<T> currentNode = rootNode;
        if (!autoCreateNode && MapUtil.isEmpty(currentNode.getChildren()) && nodePaths.length > 1) {
            return Optional.empty();
        }
        for (int i = 1; i < nodePaths.length; i++) {
            final String nodeName = nodePaths[i];
            final Map<String, NestedSetModelTreeNode<T>> childNodes = currentNode.getChildren();
            final NestedSetModelTreeNode<T> parentNode = currentNode;
            currentNode = autoCreateNode ? childNodes.computeIfAbsent(nodeName, (name) -> new NestedSetModelTreeNode<>(nodeName, parentNode)) : childNodes.get(nodeName);
            if (currentNode == null) {
                break;
            }
        }
        return Optional.ofNullable(currentNode);
    }

    public void scoreCalc() {
        this.scoreCalc(0);
    }

    public void scoreCalc(int startScore) {
        this.scoreCalc(startScore, 1);
    }

    public void scoreCalc(int startScore, int stepLength) {
        AtomicInteger counter = new AtomicInteger(startScore);
        this.rootNode.scoreCalc(counter, stepLength);
    }

    public static class Builder<T> {
        private final NestedSetModelTree<T> tree;

        private Builder() {
            tree = new NestedSetModelTree<>();
        }


        public void addNode(final T data, String... nodePaths) {
            this.addNode(data, 0, nodePaths);
        }

        public void addNode(final T data, final int sort, String... nodePaths) {
            tree.addNode(data, sort, nodePaths);
        }

        public NestedSetModelTree<T> build() {
            return this.build(0);
        }

        public NestedSetModelTree<T> build(int startScore) {
            return this.build(startScore, 1);
        }

        public NestedSetModelTree<T> build(int startScore, int stepLength) {
            this.tree.scoreCalc(startScore, stepLength);
            return this.tree;
        }
    }
}
