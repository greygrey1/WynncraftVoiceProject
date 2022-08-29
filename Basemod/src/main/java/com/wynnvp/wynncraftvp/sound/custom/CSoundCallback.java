package com.wynnvp.wynncraftvp.sound.custom;

import javax.sound.sampled.SourceDataLine;

public interface CSoundCallback {

    void ready(byte[] audioData, float volume);

}
