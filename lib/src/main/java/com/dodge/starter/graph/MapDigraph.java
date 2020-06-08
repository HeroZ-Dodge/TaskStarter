package com.dodge.starter.graph;


import android.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class MapDigraph<V> implements IDirectGraph<V> {

    private Map<V, GraphNode<V>> map;

    public MapDigraph() {
        this.map = new HashMap<>();
    }


    @Override
    public Set<V> nodeSet() {
        return map.keySet();
    }

    @Override
    public void add(V v) {
        if (v == null) {
            return;
        }
        GraphNode<V> node = map.get(v);
        if (node == null) {
            node = new GraphNode<>(v);
            map.put(v, node);
        }
    }

    @Override
    public boolean remove(V v) {
        if (v == null) {
            return false;
        }
        // 移除节点
        GraphNode<V> graphNode = map.remove(v);
        if (graphNode == null) {
            return false;
        }
        // 清空当前节点的边数据
        Map<V, Edge<V>> edgeMap = graphNode.edgeMap;
        for (V item : edgeMap.keySet()) {
            GraphNode<V> node = map.get(item);
            if (node != null) {
                node.removeInDegree(v);
            }
        }
        // 清空其他节点的边数据
        for (GraphNode<V> node : map.values()) {
            node.removeEdge(v);
        }
        return true;
    }

    @Override
    public void addEdge(Edge<V> edge) {
        if (edge == null || edge.getFrom() == null || edge.getTo() == null) {
            return;
        }
        GraphNode<V> fromNode = getGraphNode(edge.getFrom()); // get node
        GraphNode<V> toNode = getGraphNode(edge.getTo()); // get node
        if (fromNode == null || toNode == null) {
            throw new IllegalStateException("Graph Node is null");
        }
        boolean result = fromNode.addEdge(edge);
        if (result) {
            toNode.addInDegree(edge.getFrom());
        } else {
            throw new IllegalStateException("has add this edge, please remove the old edge");
        }
    }

    @Override
    public void addEdge(V from, V to) {
        this.addEdge(new Edge<>(from, to));
    }

    @Override
    public boolean removeEdge(Edge<V> edge) {
        if (edge == null || edge.getFrom() == null || edge.getTo() == null) {
            return false;
        }
        GraphNode<V> fromNode = getGraphNode(edge.getFrom()); // get node
        GraphNode<V> toNode = getGraphNode(edge.getTo()); // get node
        if (fromNode == null || toNode == null) {
            throw new IllegalStateException("Graph Node is null");
        }
        // 移除边数据
        Edge<V> removeItem = fromNode.removeEdge(edge.getTo());
        if (removeItem != null) {
            toNode.removeInDegree(edge.getFrom());
            return true;
        } else {
            return false;
        }
    }


    @Override
    public boolean removeEdge(V from, V to) {
        return removeEdge(new Edge<>(from, to));
    }



    public Set<V> getInDegreeSet(V v) {
        GraphNode<V> node = map.get(v);
        return node != null ? node.inDegreeSet : Collections.emptySet();
    }

    public Set<V> getOutDegreeSet(V v) {
        GraphNode<V> node = map.get(v);
        return node != null ? node.edgeMap.keySet() : Collections.emptySet();
    }


    /**
     * 获取节点
     */
    private GraphNode<V> getGraphNode(V v) {
        return map.get(v);
    }

    @Override
    public Pair<Boolean, List<V>> topologicalSort() {
        //入度为0的结点队列
        Queue<V> zeroInDegreeVertexQueue = new LinkedList<>();
        //保存结果
        List<V> resultList = new ArrayList<>();
        //保存入度不为0的结点
        Map<V, Integer> notZeroInDegreeVertexMap = new HashMap<>();

        //扫描所有的顶点,将入度为0的顶点入队列
        for (Map.Entry<V, GraphNode<V>> item : map.entrySet()) {
            V vertex = item.getKey();
            GraphNode<V> graphNode = item.getValue();
            int inDegree = graphNode.inDegree;
            if (inDegree == 0) {
                zeroInDegreeVertexQueue.add(vertex);
                resultList.add(vertex);
            } else {
                notZeroInDegreeVertexMap.put(vertex, inDegree);
            }
        }

        //扫描完后，没有入度为0的结点，说明有环，直接返回
        if (zeroInDegreeVertexQueue.isEmpty()) {
            return new Pair<>(false, resultList);
        }

        //采用topology算法, 删除入度为0的结点和它的关联边
        while (!zeroInDegreeVertexQueue.isEmpty()) {
            V v = zeroInDegreeVertexQueue.poll();
            //节点
            GraphNode<V> graphNode = getGraphNode(v);
            //得到相邻结点
            Set<V> subsequentNodes = graphNode.edgeMap.keySet();

            for (V subsequentVertex : subsequentNodes) {
                Integer degree = notZeroInDegreeVertexMap.get(subsequentVertex);
                if (degree != null) {
                    if (--degree == 0) {
                        resultList.add(subsequentVertex);
                        zeroInDegreeVertexQueue.add(subsequentVertex);
                        notZeroInDegreeVertexMap.remove(subsequentVertex);
                    } else {
                        notZeroInDegreeVertexMap.put(subsequentVertex, degree);
                    }
                }
            }
        }

        //notZeroIndegreeVertexMap如果为空, 表示没有环
        boolean sortResult = notZeroInDegreeVertexMap.size() == 0;
        return new Pair<>(sortResult, resultList);
    }


    /**
     * 节点
     * 包含内容 和 边
     */
    private static class GraphNode<V> {

        private int inDegree;
        private V value;
        private Set<V> inDegreeSet;      // 入度
        private Map<V, Edge<V>> edgeMap; // 出度

        private GraphNode(V value) {
            this.value = value;
            this.edgeMap = new HashMap<>(8);
            this.inDegreeSet = new HashSet<>();
        }

        private Edge<V> getEdge(V to) {
            return to != null ? edgeMap.get(to) : null;
        }


        private boolean addEdge(Edge<V> edge) {
            V v = edge.getTo();
            if (edgeMap.containsKey(v)) {
                return false;
            } else {
                edgeMap.put(v, edge);
                return true;
            }
        }

        private Edge<V> removeEdge(V to) {
            return to != null ? edgeMap.remove(to) : null;
        }


        private void addInDegree(V v) {
            inDegreeSet.add(v);
            inDegree += 1;
        }

        private void removeInDegree(V v) {
            inDegreeSet.remove(v);
            inDegree -= 1;
        }



    }


}
