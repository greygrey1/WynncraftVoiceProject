package com.wynnvp.wynncraftvp.sound.custom.thread;

import com.wynnvp.wynncraftvp.sound.custom.CSoundThread;
import com.wynnvp.wynncraftvp.sound.custom.ICSound;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

import static javax.sound.sampled.AudioSystem.getAudioInputStream;

public class MonoThread extends CSoundThread implements ICSound {

    private final File file;

    public MonoThread(File file) {
        setDaemon(true);
        setName("VoW - Mono Audio");
        this.file = file;
        this.stopped = false;
    }

    @Override
    public void run() {
        try (final AudioInputStream in = AudioSystem.getAudioInputStream(file)) {
            final AudioFormat monoFormat = getMonoFormat(in.getFormat().getSampleRate());
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, monoFormat);
            try (final SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info)) {
                if (line != null) {
                    line.open(monoFormat);
                    line.start();
                    read(getAudioInputStream(monoFormat, in), line);
                    line.drain();
                    line.stop();
                }
            } catch (LineUnavailableException e) {
                e.printStackTrace();
            }
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
    }

}
