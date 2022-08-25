package com.wynnvp.wynncraftvp.sound;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

import static javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED;
import static javax.sound.sampled.AudioSystem.getAudioInputStream;

public class SoundController {

    public void playAtPlayer(File file) {
        final Thread musicThread = new Thread(() -> {
            try (final AudioInputStream in = AudioSystem.getAudioInputStream(file)) {
                final AudioFormat outFormat = getOutFormat(in.getFormat());
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, outFormat);
                try (final SourceDataLine line =
                             (SourceDataLine) AudioSystem.getLine(info)) {
                    if (line != null) {
                        line.open(outFormat);
                        line.start();
                        stream(getAudioInputStream(outFormat, in), line);
                        line.drain();
                        line.stop();
                    }
                } catch (LineUnavailableException e) {
                    e.printStackTrace();
                }
            } catch (UnsupportedAudioFileException | IOException e) {
                e.printStackTrace();
            }
        });
        musicThread.setName("VoW - Sound Player");
        musicThread.start();
    }

    private AudioFormat getOutFormat(AudioFormat inFormat) {
        final int ch = inFormat.getChannels();
        final float rate = inFormat.getSampleRate();
        return new AudioFormat(PCM_SIGNED, rate, 16, ch, ch * 2, rate, false);
    }

    private void stream(AudioInputStream in, SourceDataLine line)
            throws IOException {
        final byte[] buffer = new byte[65536];
        for (int n = 0; n != -1; n = in.read(buffer, 0, buffer.length)) {
            line.write(buffer, 0, n);
        }
    }

}
