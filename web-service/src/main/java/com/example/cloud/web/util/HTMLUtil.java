package com.example.cloud.web.util;

import org.apache.commons.lang.StringEscapeUtils;


/**
 * @author θ¨ζ
 * @date 2018/5/18 δΈε10:17
 */
public class HTMLUtil {

    public static String html2text(String str) {
        str = str.replaceAll("\\<.*?\\>", "");
        str = StringEscapeUtils.unescapeHtml(str);
        return str;
    }

}
