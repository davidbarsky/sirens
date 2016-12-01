package com.davidbarsky.tree;

import java.util.Optional;
import java.util.TreeSet;

public class Node<T extends Comparable<T>> implements Comparable<Node> {
    private Optional<Node<T>> parent;
    private TreeSet<Node<T>> children;

    private T value;
    public Integer minCost;
    public Integer maxCost;

    public Node(Optional<Node<T>> parent, T value, Integer minCost, Integer maxCost) {
        this.parent = parent;
        this.value = value;
        this.minCost = minCost;
        this.maxCost = maxCost;
        this.children = new TreeSet<>();
    }

    public void insert(T value, Integer minCost, Integer maxCost) throws TreeException {
        if (!parent.isPresent()) {
            this.parent = Optional.of(new Node<>(Optional.empty(), value, minCost, maxCost));
        }

        this.children.add(new Node<>(Optional.of(this), value, minCost, maxCost));
    }

    @Override
    public int compareTo(Node that) {
        if (this.minCost < that.minCost) {
            return -1;
        } else if (this.minCost == that.minCost) {
            return 0;
        } else {
            return 1;
        }
    }
}
