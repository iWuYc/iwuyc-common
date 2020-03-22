package com.iwuyc.tools.commons.util.string;

/**
 * 该类引自:https://blog.csdn.net/u012834750/article/details/80005162 感谢网友分享。
 * <pre>
 * 实验统计
 * <p>
 * 使用网上提供的一份英语单词文件：http://www.cs.duke.edu/~ola/ap/linuxwords,共45402个单词,分别比较上面每一个算法在哈希表长度为100,1000和10000时的最大冲突数，理论上平均为455,46和5。结果如下：
 *
 * <table>
 * <thead>
 * <tr>
 *   <th>算法</th>
 *   <th align="center">长度100的哈希</th>
 *   <th align="right">长度1000的哈希</th>
 *   <th>长度10000的哈希</th>
 * </tr>
 * </thead>
 * <tbody><tr>
 *   <td>bkdrhash</td>
 *   <td align="center">509</td>
 *   <td align="right">72</td>
 *   <td>14</td>
 * </tr>
 * <tr>
 *   <td>aphash</td>
 *   <td align="center">519</td>
 *   <td align="right">72</td>
 *   <td>15</td>
 * </tr>
 * <tr>
 *   <td>jshash</td>
 *   <td align="center">494</td>
 *   <td align="right">66</td>
 *   <td>15</td>
 * </tr>
 * <tr>
 *   <td>rshash</td>
 *   <td align="center">505</td>
 *   <td align="right">74</td>
 *   <td>15</td>
 * </tr>
 * <tr>
 *   <td>sdbmhash</td>
 *   <td align="center">518</td>
 *   <td align="right">67</td>
 *   <td>15</td>
 * </tr>
 * <tr>
 *   <td>pjwhash</td>
 *   <td align="center">756</td>
 *   <td align="right">131</td>
 *   <td>34</td>
 * </tr>
 * <tr>
 *   <td>elfhash</td>
 *   <td align="center">801</td>
 *   <td align="right">158</td>
 *   <td>91</td>
 * </tr>
 * <tr>
 *   <td>djbhash</td>
 *   <td align="center">512</td>
 *   <td align="right">64</td>
 *   <td>17</td>
 * </tr>
 * <tr>
 *   <td>dekhash</td>
 *   <td align="center">536</td>
 *   <td align="right">75</td>
 *   <td>22</td>
 * </tr>
 * <tr>
 *   <td>bphash</td>
 *   <td align="center">1391</td>
 *   <td align="right">696</td>
 *   <td>690</td>
 * </tr>
 * <tr>
 *   <td>fnvhash</td>
 *   <td align="center">516</td>
 *   <td align="right">65</td>
 *   <td>14</td>
 * </tr>
 * <tr>
 *   <td>javahash</td>
 *   <td align="center">523</td>
 *   <td align="right">69</td>
 *   <td>16</td>
 * </tr>
 * </tbody></table>
 * <p>
 * 结论：
 * 从上面的统计数据可以看出对英文单词集而言，jshash,djbhash和fnvhash都有很好地分散性。
 * </pre>
 */
@SuppressWarnings("unused")
public class StringHashUtils {
    /**
     * BKDRHash是Kernighan和Dennis在《The C programming language》中提出的。这个算法的常数131是如何选取的，尚未可知，有知情者可以留言。
     *
     * @param str 待计算的字符串
     * @return hash值
     */
    public static int bkdrHash(String str) {
        final int seed = 131;

        int hash = 0;

        for (int i = 0; i < str.length(); i++) {
            hash = hash * seed + (int) str.charAt(i);
        }

        return hash & 0x7FFFFFFF;
    }

    /**
     * Arash Partow提出了这个算法，声称具有很好地分布性。
     *
     * @param str 待计算的字符串
     * @return hash值
     */
    public static int apHash(String str) {
        int hash = 0;

        for (int i = 0; i < str.length(); i++) {
            if ((i & 1) == 0) {
                hash ^= (hash << 7) ^ (str.charAt(i)) ^ (hash >> 3);
            } else {
                hash ^= ~((hash << 11) ^ (str.charAt(i)) ^ (hash >> 5));
            }
        }

        return hash & 0x7FFFFFFF;
    }

    /**
     * Justin Sobel提出的基于位的函数函数。
     *
     * @param str 待计算的字符串
     * @return hash值
     */
    public static int jsHash(String str) {
        int hash = 0;

        for (int i = 0; i < str.length(); i++) {
            hash ^= (hash << 5) + (int) str.charAt(i) + (hash >> 2);
        }

        return hash & 0x7FFFFFFF;
    }

    /**
     * 其作者是Robert Sedgwicks。
     *
     * @param str 待计算的字符串
     * @return hash值
     */
    public static int rsHash(String str) {
        int hash = 0;

        int a = 63689;
        final int b = 378551;

        for (int i = 0; i < str.length(); i++) {
            hash = hash * a + (int) str.charAt(i);
            a *= b;
        }

        return hash & 0x7FFFFFFF;
    }

    /**
     * SDBM项目使用的哈希函数，声称对所有的数据集有很好地分布性。
     *
     * @param str 待计算的字符串
     * @return hash值
     */
    public static int sdbmHash(String str) {
        int hash = 0;

        for (int i = 0; i < str.length(); i++) {
            hash = (int) str.charAt(i) + (hash << 6) + (hash << 16) - hash;
        }

        return hash & 0x7FFFFFFF;
    }

    /**
     * Peter J. Weinberger在其编译器著作中提出的。
     *
     * @param str 待计算的字符串
     * @return hash值
     */
    public static int pjwHash(String str) {
        int BitsInUnignedInt = 32;
        int ThreeQuarters = 24;
        int OneEighth = 4;
        int HighBits = 0xFFFFFFFF << (BitsInUnignedInt - OneEighth);
        int hash = 0;
        int test;

        for (int i = 0; i < str.length(); i++) {
            hash = (hash << OneEighth) + (int) str.charAt(i);
            if ((test = hash & HighBits) != 0) {
                hash = ((hash ^ (test >> ThreeQuarters)) & (~HighBits));
            }
        }

        return hash & 0x7FFFFFFF;
    }

    /**
     * Unix系统上面广泛使用的哈希函数。
     *
     * @param str 待计算的字符串
     * @return hash值
     */
    public static int elfHash(String str) {
        int hash = 0;
        int x = 0;

        for (int i = 0; i < str.length(); i++) {
            hash = (hash << 4) + (int) str.charAt(i);

            if ((x & hash & 0xF0000000L) != 0) {
                hash ^= x >> 24;
                hash &= ~x;
            }
        }

        return hash & 0x7FFFFFFF;
    }

    /**
     * Daniel J. Bernstein在comp.lang.c邮件列表中发表的，是距今为止比较高效的哈希函数之一。
     *
     * @param str 待计算的字符串
     * @return hash值
     */
    public static int djbHash(String str) {
        int hash = 5381;

        for (int i = 0; i < str.length(); i++) {
            hash += (hash << 5) + (int) str.charAt(i);
        }

        return hash & 0x7FFFFFFF;
    }

    /**
     * Donald E. Knuth在《计算机程序设计的艺术》中提出的哈希函数。
     *
     * @param str 待计算的字符串
     * @return hash值
     */
    public static int dekHash(String str) {
        int hash = str.length();

        for (int i = 0; i < str.length(); i++) {
            hash = (hash << 5) ^ (hash >> 27) ^ (int) str.charAt(i);
        }

        return hash & 0x7FFFFFFF;
    }

    /**
     * BPHash
     *
     * @param str 待计算的字符串
     * @return hash值
     */
    public static int bpHash(String str) {
        int hash = str.length();

        for (int i = 0; i < str.length(); i++) {
            hash = (hash << 7) ^ (int) str.charAt(i);
        }

        return hash & 0x7FFFFFFF;
    }

    /**
     * FNVHash
     *
     * @param str 待计算的字符串
     * @return hash值
     */
    public static int fnvHash(String str) {
        int fnvprime = 0x811C9DC5;
        int hash = 0;

        for (int i = 0; i < str.length(); i++) {
            hash *= fnvprime;
            hash ^= str.charAt(i);
        }

        return hash & 0x7FFFFFFF;
    }

    /**
     * 这是Java的字符串类的Hash算法，简单实用高效。直接从JDK6里面拿出来的代码。
     *
     * @param str 待计算的字符串
     * @return hash值
     */
    public static int javaHash(String str) {
        int hash = 0;

        for (int i = 0; i < str.length(); i++) {
            hash = hash * 31 + (int) str.charAt(i);
        }

        return hash & 0x7FFFFFFF;
    }
}
