package com.wynnvp.wynncraftvp.sound.custom.thread;

import com.wynnvp.wynncraftvp.sound.custom.CSoundThread;
import net.minecraft.client.Minecraft;
import net.minecraft.util.SoundCategory;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

import static com.wynnvp.wynncraftvp.utils.Utils.*;
import static javax.sound.sampled.AudioSystem.getAudioInputStream;

public class MonoThread extends CSoundThread {

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
                    stream(getAudioInputStream(monoFormat, in), line);
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

    private void stream(AudioInputStream audioInputStream, SourceDataLine sourceDataLine) throws IOException {
        try {
            byte[] audioData;
            while ((audioData = convertForData(audioInputStream)) != null) {
                float volume = minecraft().gameSettings.getSoundLevel(SoundCategory.VOICE);
                byte[] mono = adjustVolumeMono(audioData, volume);
                sourceDataLine.write(mono, 0, mono.length);
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

}
