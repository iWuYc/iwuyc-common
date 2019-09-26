package com.iwuyc.tools.commons.basic;

import com.iwuyc.tools.commons.util.collection.CollectionUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

public class CollectionUtilTest {

    @Test
    public void isEmpty() {
        Collection<Object> coll = null;
        Assert.assertTrue(CollectionUtil.isEmpty(coll));
        coll = new ArrayList<>();
        Assert.assertTrue(CollectionUtil.isEmpty(coll));
        coll.add(new Object());
        Assert.assertFalse(CollectionUtil.isEmpty(coll));

        Assert.assertTrue(CollectionUtil.isNotEmpty(coll));

        System.out.println(CollectionUtil.class.getName());
    }

}