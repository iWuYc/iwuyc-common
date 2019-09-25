package com.iwuyc.tools.commons.util.crypto;

import org.junit.Test;

public class HashUtilTest {

    @Test
    public void sha1() {
        System.out.println(HashUtil.sha1("123456"));
    }
}