package com.andrewvora.apps.rideatlanta.utils;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.andrewvora.apps.rideatlanta.R;

/**
 * Created on 4/19/2017.
 *
 * @author Andrew Vorakrajangthiti
 */

public final class WordUtils {

    @StringRes
    public static int getFullDirectionString(@NonNull String direction) {
        switch (direction) {
            case "E": return R.string.direction_east;
            case "W": return R.string.direction_west;
            case "N": return R.string.direction_north;
            case "S": return R.string.direction_south;
        }

        return R.string.direction_unknown;
    }

    public static String capitalizeWords(@NonNull String word) {
        String[] tokens = word.split("[\\s]");
        StringBuilder capitalizedWord = new StringBuilder();

        for(String token : tokens) {
            if(token.contains("-")) {
                capitalizedWord.append(" ").append(capitalizeHyphenatedWords(token));
            }
            else if(token.length() > 1) {
                capitalizedWord
						.append(" ")
						.append(token.substring(0, 1).toUpperCase())
						.append(token.substring(1, token.length()).toLowerCase());
            }
            else {
                capitalizedWord.append(" ").append(token.toUpperCase());
            }
        }

        return capitalizedWord.toString().trim();
    }

    private static String capitalizeHyphenatedWords(@NonNull String words) {
        String[] tokens = words.split("\\-");
        StringBuilder result = new StringBuilder();

        for(String token : tokens) {
            if(token.length() > 1) {
                result.append(token.substring(0, 1).toUpperCase())
						.append(token.substring(1, token.length()).toLowerCase())
						.append("-");
            }
            else {
                result.append(token.toUpperCase()).append("-");
            }
        }

        return result.substring(0, result.length()-1);
    }
}
