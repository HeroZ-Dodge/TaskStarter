package com.dodge.starter.graph;


import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * 边的信息
 */
public class Edge<V> {

    /**
     * 开始节点
     */
    private V from;

    /**
     * 结束节点
     */
    private V to;

    /**
     * 权重
     */
    private double weight;

    public Edge(V from, V to) {
        this.from = from;
        this.to = to;
    }

    public V getFrom() {
        return from;
    }

    public void setFrom(V from) {
        this.from = from;
    }

    public V getTo() {
        return to;
    }

    public void setTo(V to) {
        this.to = to;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @NotNull
    @Override
    public String toString() {
        return "Edge{" +
                "from=" + from +
                ", to=" + to +
                ", weight=" + weight +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Edge<?> edge = (Edge<?>) o;
        return Double.compare(edge.weight, weight) == 0 &&
                Objects.equals(from, edge.from) &&
                Objects.equals(to, edge.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, weight);
    }
}