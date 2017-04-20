package com.andrewvora.apps.rideatlanta.utils;

import android.support.annotation.NonNull;

/**
 * Created on 4/19/2017.
 *
 * @author Andrew Vorakrajangthiti
 */

public final class WordUtils {

    public static String capitalizeWords(@NonNull String word) {
        String[] tokens = word.split("[\\s]");
        String capitalizedWord = "";

        for(String token : tokens) {
            if(token.contains("-")) {
                capitalizedWord += " " + capitalizeHyphenatedWords(token);
            }
            else if(token.length() > 1) {
                capitalizedWord += " " +
                        token.substring(0, 1).toUpperCase() +
                        token.substring(1, token.length()).toLowerCase();
            }
            else {
                capitalizedWord += " " + token.toUpperCase();
            }
        }

        return capitalizedWord.trim();
    }

    private static String capitalizeHyphenatedWords(@NonNull String words) {
        String[] tokens = words.split("\\-");
        String result = "";

        for(String token : tokens) {
            if(token.length() > 1) {
                result += token.substring(0, 1).toUpperCase() +
                        token.substring(1, token.length()).toLowerCase() + "-";
            }
            else {
                result += token.toUpperCase() + "-";
            }
        }

        return result.substring(0, result.length()-1);
    }
}
