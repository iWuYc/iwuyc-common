该项目使用了JDK8中的lambda表达式，所以，最低得使用JDK8以上版本。
#### Abstract
Deprecated. 废弃，不建议再使用。GitHub有一个开源的类路径扫描工具包，建议使用该工具包。
 
扫描包含指定注解的类，并返回这些类。 突然间发现GitHub有一个 FastClasspathScanner 包可以做到更加强大的功能，也更加稳定。

### Fast Classpath Scanner
#### Maven POM Archive
<!-- https://mvnrepository.com/artifact/io.github.lukehutch/fast-classpath-scanner -->
&lt;dependency&gt;
    &lt;groupId&gt;io.github.lukehutch&lt;/groupId&gt;
    &lt;artifactId&gt;fast-classpath-scanner&lt;/artifactId&gt;
    &lt;version&gt;${classpath.scanner.version}&lt;/version&gt;
&lt;/dependency&gt;
