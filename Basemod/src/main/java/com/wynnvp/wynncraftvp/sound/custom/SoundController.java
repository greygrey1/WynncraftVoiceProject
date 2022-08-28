package com.wynnvp.wynncraftvp.sound.custom;

import com.wynnvp.wynncraftvp.sound.custom.thread.MonoThread;
import com.wynnvp.wynncraftvp.sound.custom.thread.StereoThread;
import net.minecraft.util.math.Vec3d;

import java.io.File;
import java.util.HashMap;

public class SoundController {

    public static final HashMap<SoundType, CSoundThread> cSoundThreads = new HashMap<>();

    public static MonoThread loadMono(File file) {
        MonoThread monoThread = new MonoThread(file);
        cSoundThreads.put(SoundType.MONO, monoThread);
        return monoThread;
    }

    public static StereoThread loadStereo(File file, Vec3d position) {
        StereoThread stereoThread = new StereoThread(position, file);
        cSoundThreads.put(SoundType.STEREO, stereoThread);
        return stereoThread;
    }

    public static StereoThread loadStereo(File file, String rawName) {
        StereoThread stereoThread = new StereoThread(rawName, file);
        cSoundThreads.put(SoundType.STEREO, stereoThread);
        return stereoThread;
    }

    public enum SoundType {
        MONO, STEREO
    }

}
