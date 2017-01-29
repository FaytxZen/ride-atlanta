package com.andrewvora.apps.rideatlanta.utils;

import android.os.Build;
import android.text.Html;

/**
 * Created by faytx on 1/29/2017.
 * @author Andrew Vorakrajangthiti
 */
public final class CompatUtil {

    private CompatUtil() {
        // prevent instantiation
    }

    public static String getDecodedHtml(String htmlString) {
        String decodedHtml;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            decodedHtml = Html.fromHtml(htmlString, Html.FROM_HTML_MODE_LEGACY).toString();
        }
        else {
            decodedHtml = Html.fromHtml(htmlString).toString();
        }

        return decodedHtml;
    }
}
