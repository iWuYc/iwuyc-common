package com.iwuyc.tools.commons.thread;

import java.util.Collection;

/**
 * @author Neil
 */
public interface ModifiableService<Parameter, ReturnType> {
    /**
     * 更新相应的服务
     *
     * @param parameter 参数
     * @return 返回更新前的数据
     */
    ReturnType update(Collection<Parameter> parameter);

    /**
     * 删除相应的服务
     *
     * @param parameter 参数
     * @return 返回删除前的数据
     */

    ReturnType delete(Collection<Parameter> parameter);

    /**
     * 添加相应的服务
     *
     * @param parameter 参数
     * @return 返回添加前的数据
     */
    ReturnType add(Collection<Parameter> parameter);
}
