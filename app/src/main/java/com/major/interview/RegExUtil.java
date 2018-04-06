package com.major.interview;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @desc: TODO
 * @author: Major
 * @since: 2018/4/6 22:26
 */
public class RegExUtil {

    private static final String regexForLink = "<\\s*link\\s+([^>]*)\\s*>";
    private static final String regexForRel = "rel=\"apple-touch-icon";
    private static final String regexForHref = "href=\"([^\"]+)\"";
    private static final String regexForUrl = "[a-zA-z]+://[^\\s]*";


    /**
     * 解析 html 获取指定的 link 的图片 url
     *
     * @param html
     * @return
     */
    public static String[] getImgs(String html) {
        if (html == null) {
            return null;
        }
        List<String> list = new ArrayList<>();
        Pattern pattern = Pattern.compile(regexForLink);
        Matcher matcher = pattern.matcher(html);
        Pattern pattern1 = Pattern.compile(regexForRel);
        Pattern pattern2 = Pattern.compile(regexForHref);
        while (matcher.find()) {
            String group = matcher.group();
            Matcher matcher1 = pattern1.matcher(group);
            if (matcher1.find()) {
                Matcher matcher2 = pattern2.matcher(group);
                boolean b = matcher2.find();
                if (b) {
                    String group1 = matcher2.group();
                    // <link rel="apple-touch-icon-precomposed" sizes="144x144" href="//tb2.bdstatic.com/tb/mobile/sglobal/layout/classic/icon/apple-touch-icon-144x144-precomposed_08a91b3.png">
                    // <link rel="apple-touch-icon" href="http://u1.sinaimg.cn/upload/h5/img/apple-touch-icon.png">
                    // <link> <link href="//gw.alicdn.com/tps/i2/TB1nmqyFFXXXXcQbFXXE5jB3XXX-114-114.png" rel="apple-touch-icon-precomposed">
                    String img = group1.replace("href=\"//", "http://").replace("href=\"", "").replace("\"", "");
                    list.add(img);
                }
            }
        }
        return list.toArray(new String[list.size()]);
    }

    public static boolean isUrl(String url) {
        if (url == null) {
            return false;
        }
        Pattern pattern = Pattern.compile(regexForUrl);
        return pattern.matcher(url).matches();
    }

}
