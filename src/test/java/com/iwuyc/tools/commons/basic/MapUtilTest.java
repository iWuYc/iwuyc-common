package com.iwuyc.tools.commons.basic;

import com.iwuyc.tools.commons.util.collection.CollectionUtil;
import com.iwuyc.tools.commons.util.collection.MapUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auth iWuYc
 * @time 2017-08-07 13:45
 * @since 2019-10-17 10:02:21
 */
public class MapUtilTest {
    private Map<String, String> map = new HashMap<>();


    @Test
    public void findKeyByVal() {
        Collection<String> val = MapUtil.findKeyByVal(map, "99");
        Assert.assertTrue(val.contains("99"));
        val = MapUtil.findKeyByVal(null, "99");
        Assert.assertTrue(CollectionUtil.isEmpty(val));

        val = MapUtil.findKeyByVal(map, "null");
        Assert.assertTrue(val.contains(null));
    }

    @Test
    public void findEntryByPrefixKey() {
        Map<String, String> result = MapUtil.findEntryByPrefixKey(map, "1");
        Assert.assertEquals(11, result.size());

        result = MapUtil.findEntryByPrefixKey(map, null);
        Assert.assertEquals(1, result.size());

        result = MapUtil.findEntryByPrefixKey(null, "1");
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void isEmpty() {
        Map<String, String> map = new HashMap<>();
        Assert.assertTrue(MapUtil.isEmpty(map));
        map.put("1", "1");
        Assert.assertFalse(MapUtil.isEmpty(map));

        Assert.assertTrue(MapUtil.isNotEmpty(map));
    }

    @Test
    public void isNotEmpty() {
    }

    /**
     *
     */
    @Before
    public void setUp() {
        map.put(null, "null");
        for (int i = 0; i < 100; i++) {
            map.put(String.valueOf(i), String.valueOf(i));
        }
    }

    @Test
    public void test() {
        Map<String, Object> map = new HashMap<>();
        map.put("null1", null);
        map.put("null2", null);
        map.put("null3", null);
        map.put("null4", "hello");

        Entity entity1 = new Entity();
        entity1.id = "id1";
        entity1.name = "jack";
        Entity entity2 = new Entity();
        entity2.id = "id2";
        entity2.name = "jack";
        Entity entity3 = new Entity();
        entity3.id = "id3";
        entity3.name = "tom";

        map.put("entity1", entity1);
        map.put("entity2", entity2);
        map.put("entity3", entity3);

        Collection<String> result = MapUtil.findKeyByVal(map, null);
        System.out.println(result);

        result = MapUtil.findKeyByVal(map, entity1);
        System.out.println(result);
    }

    public static class Entity {
        String id;
        String name;

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Entity other = (Entity) obj;
            if (name == null) {
                return other.name == null;
            }

            return name.equals(other.name);
        }

        @Override
        public String toString() {
            return "Entity [id=" + id + "]";
        }

    }

}
