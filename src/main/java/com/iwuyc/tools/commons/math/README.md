# Range
> 在编程中，可能会遇到一些需要校验数字范围的，在接口定义的时候，可以设置值范围，当值进来时，对值进行校验。例如在protobuf文件中给某个字段定义 `` [0,10)|[20,30]|(40,50] ``范围。  
> 则判定的示例如下。  

```java
@Test
public void test()
{
    Range range = Range.compiler("[0,10)|[20,30]|(40,50]");
    assertNotNull(range);
    assertFalse(range.inRange(-1));
    assertTrue(range.inRange(0));
    assertFalse(range.inRange(10));
    assertFalse(range.inRange(11));
    assertTrue(range.inRange(20));
    assertTrue(range.inRange(30));
    assertFalse(range.inRange(31));
    assertFalse(range.inRange(40));
    assertTrue(range.inRange(41));
    assertTrue(range.inRange(50));
    assertFalse(range.inRange(51));
}
```

# NumberUtils

> 数字的工具类。

#### 将数字转换为繁体字

```java
@Test
public void translatorTest() {
    long num = -1111123456789L;
    System.out.println(NumberUtils.numberTranslation(num));
}
```

> Console:
>
> 负壹万亿壹仟壹佰壹拾壹亿贰仟叁佰肆拾伍万陆仟柒佰捌拾玖
