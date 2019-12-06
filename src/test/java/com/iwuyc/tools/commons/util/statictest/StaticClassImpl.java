package com.iwuyc.tools.commons.util.statictest;

public abstract class StaticClassImpl {
    private static final long RETRY = 10;
    private static long longTime = 1;

    public void executor() {
        while (true) {
            if (longTime < RETRY) {
                System.out.println(longTime);
            } else {
                System.out.println("end");
                break;
            }
            longTime++;
        }
    }

    protected abstract void exe();
}
