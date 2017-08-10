/**
 * @Auth iWuYc
 * @since
 * @time 2017-08-04 16:08
 */
package com.iwuyc.tools.iwuyc_commons;

import org.junit.Test;

/**
 * @Auth iWuYc
 * @since
 * @time 2017-08-04 16:08
 */
public class RunnableImpl
{

    private int a = 0;

    @Test
    public void run()
    {
        // System.out.println(a);
        //
        // System.out.println(beforeAdd());
        // System.out.println(a);
        //
        // System.out.println(afterAdd());
        // System.out.println(a);
        //
        // System.out.println(finalAdd());
        // System.out.println(a);

        System.out.println(s());
    }

    private int beforeAdd()
    {
        return ++a;
    }

    private int afterAdd()
    {
        return a++;
    }

    private int finalAdd()
    {
        try
        {
            return a++;
        }
        finally
        {
            a++;
        }
    }

    private int s()
    {
        int i = 0;
        try
        {
            return i++;
        }
        finally
        {
            System.out.println(i);
        }
    }
}
