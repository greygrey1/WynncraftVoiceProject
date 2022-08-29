package com.wynnvp.wynncraftvp.sound.custom;

import com.wynnvp.wynncraftvp.sound.custom.thread.MonoThread;
import com.wynnvp.wynncraftvp.sound.custom.thread.StereoThread;
import net.minecraft.util.math.Vec3d;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class SoundController {

    public static final Set<CSoundThread> cSoundThreads = new HashSet<>();

    public static MonoThread loadMono(File file) {
        MonoThread monoThread = new MonoThread(file);
        cSoundThreads.add(monoThread);
        return monoThread;
    }

    public static StereoThread loadStereo(File file, Vec3d position) {
        StereoThread stereoThread = new StereoThread(position, file);
        cSoundThreads.add(stereoThread);
        return stereoThread;
    }

    public static StereoThread loadStereo(File file, String rawName) {
        StereoThread stereoThread = new StereoThread(rawName, file);
        cSoundThreads.add(stereoThread);
        return stereoThread;
    }

}
