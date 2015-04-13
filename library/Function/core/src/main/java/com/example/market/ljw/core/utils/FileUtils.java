package com.example.market.ljw.core.utils;

import java.io.File;

/**
 * Created by GYH on 2014/10/20.
 */
public class FileUtils {

    /**
     * 根据文件绝对路径获取文件名
     *
     * @param filePath
     * @return
     */
    public static String getFileName(String filePath) {
        if (StringUtils.isEmpty(filePath)) return "";
        return filePath.substring(filePath.lastIndexOf(File.separator) + 1);
    }
}
