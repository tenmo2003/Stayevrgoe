package com.group12.stayevrgoe.shared.utils;

import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * @author anhvn
 */
@UtilityClass
public class FileUtils {
    public static String getProjectRootDirectory() {
        return System.getProperty("user.dir");
    }

    public static byte[] getFileBytesFromPath(String path) throws IOException {
        File keyFile = new File(path);
        return Files.readAllBytes(keyFile.toPath());
    }
}
