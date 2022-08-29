package com.wynnvp.wynncraftvp.sound;

import com.wynnvp.wynncraftvp.ModCore;
import com.wynnvp.wynncraftvp.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.wynnvp.wynncraftvp.utils.Utils.copy;

public class SoundLoader {

    //kingsrecruit-aledar-1.ogg

    public static void loadSounds(Set<String> ids) {
        ids.forEach(id -> {
            try (InputStream inputStream = SoundLoader.class.getResourceAsStream("/assets/wynnvp/sounds/" + id + ".ogg")) {
                if (inputStream != null) {
                    File parent = new File(Utils.FILE_ROOT, getQuest(id));
                    if (!parent.exists())
                        parent.mkdir();
                    //C:\Users\ender\AppData\Roaming\.minecraft\wynnvp
                    File destination = new File(parent, id+".ogg");
                    if (!destination.exists()) {
                        copy(inputStream, destination.getPath());
                        System.out.println("Loaded: " + id + ".ogg from " + getQuest(id));
                    }
                }
            } catch (Throwable ignored) {
            }
        });
    }

    public static void loadSounds() {
        loadSounds(ModCore.instance.soundsHandler.ids());
    }

    private static String getQuest(String id) {
        String result = "none";
        if (id.contains("-")) {
            String[] args = id.split("-");
            result = args[0];
        }
        return result;
    }

}
