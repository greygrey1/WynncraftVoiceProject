package com.wynnvp.wynncraftvp.sound;

import com.wynnvp.wynncraftvp.ModCore;
import com.wynnvp.wynncraftvp.config.ConfigHandler;
import com.wynnvp.wynncraftvp.utils.Utils;

import java.io.File;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import static com.wynnvp.wynncraftvp.utils.Utils.copy;

public class SoundLoader {

    public static void loadSounds() {
        if (ConfigHandler.downloadedAudios) return;
        Set<String> list = new HashSet<>();
        Set<String> ids = ModCore.instance.soundsHandler.ids();
        ids.forEach(id -> {
            try (InputStream inputStream = SoundLoader.class.getResourceAsStream("/assets/wynnvp/sounds/" + id + ".ogg")) {
                if (inputStream != null) {
                    File parent = new File(Utils.FILE_ROOT, getQuest(id));
                    if (!parent.exists())
                        parent.mkdir();

                    File destination = new File(parent, id+".ogg");
                    if (!destination.exists()) {
                        copy(inputStream, destination.getPath());
                        System.out.println("Loaded: " + id + ".ogg from " + getQuest(id));
                        list.add(id);
                    }
                }
            } catch (Throwable ignored) {
            }
        });
        ids.removeAll(list);
        if (ids.isEmpty()) {
            ConfigHandler.setDownloadedAudios(true);
        }
        list.clear();
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
