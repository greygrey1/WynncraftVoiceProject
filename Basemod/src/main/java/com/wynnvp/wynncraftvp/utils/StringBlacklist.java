package com.wynnvp.wynncraftvp.utils;

import java.util.HashSet;
import java.util.Set;

public class StringBlacklist {

    private static final Set<String> blacklist = new HashSet<>();
    static {
        blacklist.add("towerofascension");
        blacklist.add("lv");
        blacklist.add("❤");
        for (int i = 0; i <= 9; i++) {
            blacklist.add(""+i);
        }
    }

    public static boolean has(String rawName) {
        return blacklist.stream().anyMatch(rawName::contains);
    }

}
