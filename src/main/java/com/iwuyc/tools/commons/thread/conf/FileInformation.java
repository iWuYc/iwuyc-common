package com.iwuyc.tools.commons.thread.conf;

import lombok.Data;

/**
 * 记录文件信息
 *
 * @author Neil
 */
@Data
public class FileInformation {
    private final String path;
    private long lastModifiedTime;
}
