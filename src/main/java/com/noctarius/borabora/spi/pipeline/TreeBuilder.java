/*
 * Copyright (c) 2016, Christoph Engelbert (aka noctarius) and
 * contributors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.noctarius.borabora.spi.pipeline;

import java.util.ArrayList;
import java.util.List;

public class TreeBuilder {

    public static void main(String[] args) {
        TreeNode one = new TreeNode("one");

        TreeNode two = new TreeNode("two");
        TreeNode three = new TreeNode("three");
        TreeNode four = new TreeNode("four");
        one.children.add(two);
        one.children.add(three);
        one.children.add(four);

        TreeNode five = new TreeNode("five");
        TreeNode six = new TreeNode("six");
        two.children.add(five);
        two.children.add(six);

        TreeNode seven = new TreeNode("seven");
        four.children.add(seven);

        TreeNode eight = new TreeNode("eight");
        TreeNode nine = new TreeNode("nine");

        seven.children.add(eight);
        seven.children.add(nine);

        Node node = transform(one);
        System.out.println(node);

        System.out.println("");
        traversal(node);
    }

    private static void traversal(Node node) {
        System.out.println(node.value);

        if (node.left != NIL) {
            traversal(node.left);
        }

        if (node.right != NIL) {
            traversal(node.right);
        }
    }

    private static Node transform(TreeNode tree) {
        Node left = NIL;
        Node right = NIL;

        List<TreeNode> children = tree.children;
        if (children.size() > 0) {
            left = transform(children, 0);
        }

        return new Node(left, right, tree.value);
    }

    private static Node transform(List<TreeNode> children, int index) {
        Node left = NIL;
        Node right = NIL;

        String value = null;
        if (index < children.size()) {
            right = transform(children, index + 1);

            TreeNode treeNode = children.get(index);
            value = treeNode.value;
            if (treeNode.children.size() > 0) {
                left = transform(treeNode.children, 0);
            }
        }

        if (left == NIL && right == NIL && value == null) {
            return NIL;
        }

        return new Node(left, right, value);
    }

    private static class TreeNode {
        private final List<TreeNode> children = new ArrayList<>();
        private final String value;

        private TreeNode(String value) {
            this.value = value;
        }
    }

    private static class Node {
        private final Node left;
        private final Node right;
        private final String value;

        private Node(Node left, Node right, String value) {
            this.left = left;
            this.right = right;
            this.value = value;
        }

        @Override
        public String toString() {
            return "Node { left=" + left + ", right=" + right + ", value=" + value + " }";
        }
    }

    private static final Node NIL = new Node(null, null, null);

}
