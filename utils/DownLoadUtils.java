package cn.gs.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Base64;


public class DownLoadUtils {

    public static String getFileName(String agent, String fileName) throws UnsupportedEncodingException {
        if (agent.contains("MSIE")) {
            // IE浏览器
            fileName = URLEncoder.encode(fileName, "utf-8");
            fileName = fileName.replace("+", " ");
        } else if (agent.contains("Firefox")) {
            // 火狐浏览器
            Base64.Encoder encoder = Base64.getEncoder();
            byte[] textByte = fileName.getBytes("UTF-8");
            fileName = "=?utf-8?B?" + encoder.encodeToString(textByte) + "?=";

        } else {
            // 其它浏览器
            fileName = URLEncoder.encode(fileName, "utf-8");
        }
        return fileName;
    }
}
