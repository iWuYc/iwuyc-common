该项目使用了JDK8中的lambda表达式，所以，最低得使用JDK8以上版本。

- [ThreadPoolsFactory](src/main/java/com/iwuyc/tools/commons/thread/README.md)
> 在项目开发中，为了提高性能，我们通常会使用提高并发数的方式进行，这时通常是用线程池的方式进行线程管理与配置的。在项目中，为了方便集中管理线程池，我在这里使用了类似于log4j的方式进行配置。

- [Math](src/main/java/com/iwuyc/tools/commons/math/README.md)  
> - Range:在编程中，可能会遇到一些需要校验数字范围的，在接口定义的时候，可以设置值范围，当值进来时，对值进行校验。例如在protobuf文件中给某个字段定义 `` [0,10)|[20,30]|(40,50] ``范围。则判定的示例如下。  
> - MathUtils.numberTranslation(long):将数字转换为繁体字

