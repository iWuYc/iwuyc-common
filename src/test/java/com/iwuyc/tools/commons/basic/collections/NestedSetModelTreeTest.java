package com.iwuyc.tools.commons.basic.collections;

import org.junit.Test;

import java.util.Optional;

/**
 * 功能说明
 *
 * @author 吴宇春
 * @version 1.0.0
 * @date 2022/6/13
 */
public class NestedSetModelTreeTest {

    @Test
    public void addNode() {
        final NestedSetModelTree<String> tree = new NestedSetModelTree<>();
//        tree.setAutoCreateNode(false);
        tree.addNode("/1", "1");
        final Optional<NestedSetModelTreeNode<String>> targetNode = tree.getNode("1");
        tree.addNode("/1/2/3", "1", "2", "3");
        tree.addNode("/1/2/03", "1", "2", "3");
        System.out.println(tree);
    }
}