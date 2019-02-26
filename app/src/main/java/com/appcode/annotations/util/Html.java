package com.appcode.annotations.util;

public class Html {
    public static String stripHtml(String html) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return android.text.Html.fromHtml(html, android.text.Html.FROM_HTML_MODE_LEGACY).toString();
        } else {
            return android.text.Html.fromHtml(html).toString();
        }
    }
}
