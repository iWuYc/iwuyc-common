package com.iwuyc.tools.commons.thread;

import com.iwuyc.tools.commons.classtools.ClassUtils;
import com.iwuyc.tools.commons.thread.conf.FileInformation;
import com.iwuyc.tools.commons.util.file.FileUtil;
import com.iwuyc.tools.commons.util.file.PropertiesFileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.locks.ReadWriteLock;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ThreadConfig.class, PropertiesFileUtils.class, Math.class, FileInformation.class})
public class ThreadConfigTest {

    private ThreadPoolsService threadPoolsService;

    private String pathName = "/thread/thread_not_exists.properties";

    @Before
    public void before() throws Exception {
        this.threadPoolsService = ThreadConfig.config("classpath:/thread/thread.properties");
    }

    //    @Test
//    public void test() {
//        UsingConfig result = config.findUsingSetting(ThreadConfig.class.getName());
//        System.out.println(result);
//        // return root setting
//        result = config.findUsingSetting("org");
//        System.out.println(result);
//    }
//
//    @Test
//    public void config() {
//        URL configFileUrl = ThreadConfig.class.getResource("/thread/thread.properties");
//        File file = new File(configFileUrl.getFile());
//        ThreadPoolsService config = ThreadConfig.config(file);
//        System.out.println(config);
//    }
//
//    @Test
//    public void test1() throws Exception {
//        try (InputStream in = ThreadConfig.class.getResourceAsStream("/thread/thread.properties")) {
//            Properties properties = new Properties();
//            properties.load(in);
//        }
//    }
//
//    @Test
//    public void configException() {
//        File file = new File(pathName);
//        ThreadPoolsService config = ThreadConfig.config(file);
//        System.out.println(config);
//    }
//
//    @Test
//    public void coreSize() {
//        System.out.println(Runtime.getRuntime().availableProcessors());
//    }


    @Test
    public void autoScanTest() throws Exception {

        final Field defaultScanDelayTimesField = ClassUtils.findField(ThreadConfig.class, "DEFAULT_SCAN_DELAY_TIMES");
        defaultScanDelayTimesField.setAccessible(true);
        ClassUtils.fieldModifier(defaultScanDelayTimesField);
        defaultScanDelayTimesField.set(ThreadConfig.class, 5_000);


        final String propertiesFileLocation = FileUtil.absoluteLocation("classpath:/thread.properties");

        final Properties properties = PropertiesFileUtils.propertiesReader(propertiesFileLocation);
        PowerMockito.mockStatic(PropertiesFileUtils.class, Math.class);
        PowerMockito.when(PropertiesFileUtils.propertiesReader(ArgumentMatchers.any(File.class), (ReadWriteLock) ArgumentMatchers.isNull())).thenReturn(properties);

        PowerMockito.when(Math.max(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong())).thenReturn(5_000L);
        final int max = Math.max(1, 2);

        final ThreadPoolsService poolsService = ThreadConfig.config(propertiesFileLocation);

        final ThreadConfig threadConfig = ThreadPoolServiceHolder.getThreadPoolsService().getThreadConfig();
        final FileInformation fileInformation = threadConfig.getFileInformation();

        final FileInformation fileInformationSpy = PowerMockito.spy(fileInformation);
        Map<String, Object> fieldAndVal = new HashMap<>();
        fieldAndVal.put("fileInformation", fileInformationSpy);
        ClassUtils.injectFields(threadConfig, fieldAndVal);

        PowerMockito.when(fileInformationSpy.getLastModifiedTime()).thenAnswer(invocation -> System.currentTimeMillis());

        new Thread(() -> {

            try {
                for (int i = 0; i < 1000; i++) {
                    final ScheduledExecutorService scheduledExecutor = poolsService.getScheduledExecutor(ThreadConfigTest.class);
                    final ExecutorService executorService = poolsService.getExecutorService(this.getClass());

                    Thread.sleep(10_000);

                    properties.setProperty("thread.conf.default.maxQueueSize", String.valueOf(i + 100));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        System.in.read();
    }
}
