package com.example.dangjian_spring.utils;

import org.jsoup.safety.Safelist;

public class CustomSafelist {
    public static Safelist getCustomSafelist() {
        // 创建一个包含常用标签和属性的自定义Safelist
        return Safelist.relaxed()
                .addAttributes("img", "src", "alt", "width", "height")
                .addAttributes("a", "href", "target")
                .addAttributes("div", "style")
                .addAttributes("span", "style")
                .addAttributes("p", "style")
                .addAttributes("ul", "style")
                .addAttributes("ol", "style")
                .addAttributes("li", "style");
    }
}