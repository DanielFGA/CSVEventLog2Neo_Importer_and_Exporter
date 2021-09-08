package de.haw.eventlog2neo4j.core.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringUtils {

    public static String escapeSingleQuotes(String str) {
        return str.replaceAll("'", "\\\\'");
    }

    public static String removeSpecialSymbols(String str) {
        return str.replaceAll("[^a-zA-Z]+", "");
    }

}
