package com.wynnvp.wynncraftvp.sound.custom.test;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED;
import static javax.sound.sampled.AudioSystem.getAudioInputStream;

public class StereoTest {

    /*public static void main(String[] args) {
        File file = new File("C:/Users/ender/AppData/Roaming/.minecraft/wynnvp/kingsrecruit/kingsrecruit-caravandriver-2.ogg");
        new StereoTest().play(file);
    }*/

    public void play(File file) {
        try (AudioInputStream in = AudioSystem.getAudioInputStream(file)) {
            final AudioFormat stereoFormat = getStereoFormat(in.getFormat().getSampleRate());
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, stereoFormat);
            try {
                SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
                if (line != null) {
                    line.open(stereoFormat);
                    line.start();
                    stream(getAudioInputStream(stereoFormat, in), line);
                    line.drain();
                    line.stop();
                    line.flush();
                    line.close();
                }
            } catch (LineUnavailableException e) {
                e.printStackTrace();
            }
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
    }

    private void stream(AudioInputStream in, SourceDataLine line) {
        try {
            byte[] monoData;
            float left = 0f;
            float right = 1f;
            while ((monoData = convertForData(in)) != null) {
                byte[] stereo = adjustVolumeStereo(monoData, Math.min(left, 1f), Math.max(right, 0f));
                line.write(stereo, 0, stereo.length);
                left += 0.005f;
                right -= 0.005f;
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    private byte[] convertForData(AudioInputStream in) {
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

    protected AudioFormat getStereoFormat(float rate) {
        return new AudioFormat(PCM_SIGNED, rate, 16, 2, 4, rate, false);
    }

    public short bytesToShort(byte b1, byte b2) {
        return (short) (((b2 & 0xFF) << 8) | (b1 & 0xFF));
    }

    public byte[] adjustVolumeStereo(byte[] audio, float volumeLeft, float volumeRight) {
        for (int i = 0; i < audio.length; i += 2) {
            short audioSample = bytesToShort(audio[i], audio[i + 1]);

            audioSample = (short) (audioSample * (i % 4 == 0 ? volumeLeft : volumeRight));

            audio[i] = (byte) audioSample;
            audio[i + 1] = (byte) (audioSample >> 8);

        }
        return audio;
    }

}
