package com.iwuyc.tools.commons.util.file;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Stack;

public class CompressFileUtil {
    public static void archive(String sourcePath, String targetPathStr, String targetName, boolean isDelete,
        boolean isOverride) {
        if (!targetName.endsWith(".tar")) {
            targetName = targetName + ".tar";
        }
        Path targetPath = Paths.get(targetPathStr, targetName);
        File targetFile = targetPath.toFile();

        // 非覆盖式，直接退出
        if (targetFile.exists() && !isOverride) {
            return;
        }

        File sourceFile = Paths.get(sourcePath).toFile();
        if (!sourceFile.exists()) {
            return;
        }

        if (createFile(targetFile, isOverride)) {
            return;
        }
        new Archive(targetFile, sourceFile).archive();
    }

    private static boolean createFile(File file, boolean isOverride) {
        try {
            File dir = file.getParentFile();
            if (dir.exists() || dir.mkdirs()) {
                if (file.exists() && isOverride) {
                    if (!file.delete()) {
                        throw new IllegalArgumentException("Delete file fault.file:[" + file.getAbsolutePath() + "]");
                    }
                }
                return file.exists() || file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class Archive {
        private final Stack<File> fileStack = new Stack<>();
        private File targetFile;
        private File sourceFile;

        public void archive() {
            fileStack.push(sourceFile);
            File item;

            while (fileStack.size() > 0) {
                item = fileStack.peek();
                if (item.isFile()) {
                    fileArchive(item);
                } else {
                    dirArchive(item);
                }
                if (fileStack.size() > 0) {
                    fileStack.pop();
                }
            }

        }

        private void dirArchive(File item) {

        }

        private void fileArchive(File item) {

        }
    }
}
