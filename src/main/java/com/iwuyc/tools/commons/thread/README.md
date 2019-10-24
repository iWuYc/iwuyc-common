# ThreadPoolsFactory

在项目开发中，为了提高性能，我们通常会使用提高并发数的方式进行，这时通常是用线程池的方式进行线程管理与配置的。在项目中，为了方便集中管理线程池，我在这里使用了类似于log4j的方式进行配置。
### 配置示例：
```properties
thread.conf.default.corePoolSize=2
thread.conf.default.factory=com.iwuyc.tools.commons.thread.impl.DefaultExecutorServiceFactory
# [h - hour;m - minute;s - second;ms - millisecond;ns - nanosecond;]default:s
thread.conf.default.keepAliveTime=3600s
thread.conf.default.maximumPoolSize=16
thread.conf.default.maxQueueSize=1800
thread.conf.default.otherSetting1=1800

thread.using.root=default
thread.using.com.iwuyc=default
```
### 主要的接口有：
```java
import com.iwuyc.tools.commons.thread.ThreadPoolsService;
import com.iwuyc.tools.commons.thread.ExecutorServiceFactory;
import com.iwuyc.tools.commons.thread.Config;

```

## Quick Start
```java
package com.iwuyc.boot.server.netty.conf;

import java.io.File;
import java.util.concurrent.ExecutorService;

import org.junit.Test;

import com.iwuyc.tools.commons.thread.Config;
import com.iwuyc.tools.commons.thread.ThreadPoolsService;

public class YamlConfigTest
{

    @Test
    public void testThreadPool()
    {

        // 配置文件。可以为空，如果为空，则使用默认的配置。
        File thframeProperties = null;
        ThreadPoolsService service = Config.config(thframeProperties);
        ExecutorService pools = service.getExecutorService(getClass());
        System.out.println(pools);
    }

}

```
默认实现了jdk中的两种线程池的构造器：
```
com.iwuyc.tools.commons.thread.impl.DefaultExecutorServiceFactory;
```
上述类实现了```java com.iwuyc.tools.commons.thread.ExecutorServiceFactory```接口，如果有需要支持拓展第三方的线程池（必须实现```java.util.concurrent.ExecutorService```接口）生成，则可以继承该接口，然后在```create(ThreadPoolConfig config)```或```createSchedule(ThreadPoolConfig)```方法中实现相应的线程池构造。


### 默认配置
```properties
##################################################### Summary ######################################################
# thread.conf 以这个开头的，表示线程池的配置项，后面接的第一个单词表示线程池的名字，用于构建线程池中线程的名字。            
#   线程池名字之后的则是配置项名字现在有[corePoolSize,factory,keepAliveTime,maximumPoolSize,maxQueueSize],            
#    如果是这些项之外的额外配置，则会统一以键值的形式存储在otherSetting中，以方便使用第三方线程池的时候有额外配置。         
#                                                                                                                  
# thread.using 以这个开头，则是表示配置域下面所使用的线程池实例，值为线程池实例的名字。该规则跟log4j类似，就不再赘了。      
#                      
####################################################################################################################
thread.conf.default.corePoolSize=availableProcessors
thread.conf.default.factory=com.iwuyc.tools.commons.thread.impl.DefaultExecutorServiceFactory
# [h - hour;m - minute;s - second;ms - millisecond;ns - nanosecond;]default:s
thread.conf.default.keepAliveTime=60m
thread.conf.default.maximumPoolSize=availableProcessors*4
thread.conf.default.maxQueueSize=1800

thread.using.root=default
thread.using.com.iwuyc=default
```

