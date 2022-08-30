package com.wynnvp.wynncraftvp.sound.custom;

import net.minecraft.util.SoundCategory;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import java.io.ByteArrayOutputStream;

import static com.wynnvp.wynncraftvp.utils.Utils.inWynn;
import static com.wynnvp.wynncraftvp.utils.Utils.minecraft;
import static javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED;

public abstract class CSoundThread extends Thread {

    protected boolean stopped = false;

    protected AudioFormat getMonoFormat(float rate) {
        return new AudioFormat(PCM_SIGNED, rate, 16, 1, 2, rate, false);
    }

    protected AudioFormat getStereoFormat(float rate) {
        return new AudioFormat(PCM_SIGNED, rate, 16, 2, 4, rate, false);
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }

    protected byte[] convertForData(AudioInputStream in) {
        if (stopped) return null;
        byte[] data = new byte[0];
        try (final ByteArrayOutputStream baout = new ByteArrayOutputStream()) {
            final byte[] buffer = new byte[(int) ((48000 / 1000) * 2 * 20)];
            int c = in.read(buffer, 0, buffer.length);
            if (c == -1)
                return null;
            baout.write(buffer, 0, c);
            data = baout.toByteArray();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        return data;
    }

    protected void readWrite(AudioInputStream inputStream, CSoundCallback callback) {
        while (inWynn() && !stopped)
            callback.ready(convertForData(inputStream), minecraft().gameSettings.getSoundLevel(SoundCategory.VOICE));
    }

    public boolean isStopped() {
        return stopped;
    }
}
