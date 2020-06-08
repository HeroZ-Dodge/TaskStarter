package com.dodge.starter.graph;

import android.util.Pair;

import java.util.List;
import java.util.Set;

/**
 * 有向图接口
 * 对于定点+边的操作：
 * （1）增加
 * （2）删除
 * （3）获取
 */
public interface IDirectGraph<V> {


    Set<V> nodeSet();

    /**
     * 新增顶点
     *
     * @param v 顶点
     */
    void add(final V v);

    /**
     * 删除顶点
     *
     * @param v 顶点
     * @return 是否删除成功
     */
    boolean remove(final V v);

    /**
     * 新增边
     *
     * @param edge 边
     */
    void addEdge(final Edge<V> edge);

    void addEdge(final V from, final V to);

    /**
     * 移除边
     *
     * @param edge 边信息
     */
    boolean removeEdge(final Edge<V> edge);

    boolean removeEdge(final V from, final V to);

    Pair<Boolean, List<V>> topologicalSort();
}
