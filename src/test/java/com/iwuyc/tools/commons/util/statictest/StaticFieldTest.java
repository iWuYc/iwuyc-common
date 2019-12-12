package com.iwuyc.tools.commons.util.statictest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.support.membermodification.MemberModifier;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({StaticClassImpl.class})
public class StaticFieldTest {

    private StaticClassImpl instance;

    @Before
    public void setUp() throws Exception {
        instance = new StaticClassImpl() {
            @Override
            protected void exe() {
                super.executor();
            }
        };
    }

    @Test
    public void name() throws Exception {
        MemberModifier.field(StaticClassImpl.class, "RETRY").set(instance, 10);
        instance.exe();
    }
}
