package com.iwuyc.tools.commons.basic;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AbstractMapUtilTest {
    private Map<String, String> map = new HashMap<>();

    @Before
    public void setUp() {
        map.put(null, "null");
        for (int i = 0; i < 100; i++) {
            map.put(String.valueOf(i), String.valueOf(i));
        }
    }

    @Test
    public void findKeyByVal() {
        Collection<String> val = AbstractMapUtil.findKeyByVal(map, "99");
        Assert.assertTrue(val.contains("99"));
        val = AbstractMapUtil.findKeyByVal(null, "99");
        Assert.assertTrue(CollectionUtil.isEmpty(val));

        val = AbstractMapUtil.findKeyByVal(map, "null");
        Assert.assertTrue(val.contains(null));
    }

    @Test
    public void findEntryByPrefixKey() {
        Map<String, String> result = AbstractMapUtil.findEntryByPrefixKey(map, "1");
        Assert.assertEquals(11, result.size());

        result = AbstractMapUtil.findEntryByPrefixKey(map, null);
        Assert.assertEquals(1, result.size());

        result = AbstractMapUtil.findEntryByPrefixKey(null, "1");
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void isEmpty() {
        Map<String, String> map = null;
        Assert.assertTrue(AbstractMapUtil.isEmpty(map));
        map = new HashMap<>();
        Assert.assertTrue(AbstractMapUtil.isEmpty(map));
        map.put("1", "1");
        Assert.assertFalse(AbstractMapUtil.isEmpty(map));

        Assert.assertTrue(AbstractMapUtil.isNotEmpty(map));
    }

    @Test
    public void isNotEmpty() {
    }
}