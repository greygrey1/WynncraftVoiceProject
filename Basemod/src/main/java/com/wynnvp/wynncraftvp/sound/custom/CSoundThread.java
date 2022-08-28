package com.wynnvp.wynncraftvp.sound.custom;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.SourceDataLine;
import java.io.IOException;

import static javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED;

public abstract class CSoundThread extends Thread {

    protected boolean stopped = false;

    protected void read(AudioInputStream audioInputStream, SourceDataLine sourceDataLine, byte[] buffer) throws IOException {
        for (int i = 0; i != -1; i = audioInputStream.read(buffer, 0, buffer.length)) {
            if (stopped) break;
            sourceDataLine.write(buffer, 0, i);
        }
    }

    protected void read(AudioInputStream audioInputStream, SourceDataLine sourceDataLine) throws IOException {
        read(audioInputStream, sourceDataLine, new byte[65536]);
    }

    protected AudioFormat getMonoFormat(float rate) {
        return new AudioFormat(PCM_SIGNED, rate, 16, 1, 2, rate, false);
    }

    protected AudioFormat getStereoFormat(float rate) {
        return new AudioFormat(PCM_SIGNED, rate, 16, 2, 4, rate, false);
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }

}
