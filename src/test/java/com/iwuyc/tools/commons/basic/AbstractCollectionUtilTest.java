package com.iwuyc.tools.commons.basic;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

public class AbstractCollectionUtilTest {

    @Test
    public void isEmpty() {
        Collection<Object> coll = null;
        Assert.assertTrue(AbstractCollectionUtil.isEmpty(coll));
        coll = new ArrayList<>();
        Assert.assertTrue(AbstractCollectionUtil.isEmpty(coll));
        coll.add(new Object());
        Assert.assertFalse(AbstractCollectionUtil.isEmpty(coll));

        Assert.assertTrue(AbstractCollectionUtil.isNotEmpty(coll));
    }

}