package com.iwuyc.tools.commons.thread;

/**
 * 刷新引用对象
 *
 * @author Neil
 */
public interface RefreshDelegate<T> {
    /**
     * 刷新代理类
     *
     * @param newDelegate 新的代理类
     * @return 如果成功刷新, 则返回true, 否则返回false
     */
    boolean refresh(T newDelegate);
}
