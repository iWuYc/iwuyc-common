/**
 * @Auth iWuYc
 * @since
 * @time 2017-08-07 13:45
 */
package com.iwuyc.tools.commons.basic;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

/**
 * @Auth iWuYc
 * @since
 * @time 2017-08-07 13:45
 */
public class MapUtilTest {

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
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
                if (other.name != null) {
                    return false;
                }
            }
            else if (!name.equals(other.name)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "Entity [id=" + id + "]";
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

        Collection<String> result = AbstractMapUtil.findKeyByVal(map, null);
        System.out.println(result);

        result = AbstractMapUtil.findKeyByVal(map, entity1);
        System.out.println(result);
    }

}
