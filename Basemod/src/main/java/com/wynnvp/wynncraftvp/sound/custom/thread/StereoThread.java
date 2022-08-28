package com.wynnvp.wynncraftvp.sound.custom.thread;

import com.wynnvp.wynncraftvp.npc.NPCHandler;
import com.wynnvp.wynncraftvp.sound.custom.CSoundThread;
import com.wynnvp.wynncraftvp.sound.custom.ICSound;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.tuple.Pair;

import javax.sound.sampled.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static com.wynnvp.wynncraftvp.utils.Utils.*;
import static javax.sound.sampled.AudioSystem.getAudioInputStream;

public class StereoThread extends CSoundThread implements ICSound {

    private final EntityPlayerSP player;
    private final File file;
    private final String rawName;

    private Vec3d position;

    public StereoThread(Vec3d position, File file) {
        setDaemon(true);
        setName("VoW - Stereo Audio");
        this.position = position;
        this.player = Minecraft.getMinecraft().player;
        this.file = file;
        this.rawName = null;
        this.stopped = false;
    }

    public StereoThread(String rawName, File file) {
        setDaemon(true);
        setName("VoW - Stereo Audio");
        this.rawName = rawName;
        NPCHandler.find(rawName).ifPresent(this::setPosition);
        this.player = Minecraft.getMinecraft().player;
        this.file = file;
        this.stopped = false;
    }

    @Override
    public void run() {
        try (AudioInputStream in = AudioSystem.getAudioInputStream(file)) {
            final AudioFormat stereoFormat = getStereoFormat(in.getFormat().getSampleRate());
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, stereoFormat);
            try (SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info)) {
                if (line != null) {
                    line.open(stereoFormat);
                    line.start();
                    stream(getAudioInputStream(stereoFormat, in), line);
                    line.drain();
                    line.stop();
                    line.flush();
                }
            } catch (LineUnavailableException e) {
                e.printStackTrace();
            }
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
    }

    private void stream(AudioInputStream inputStream, SourceDataLine dataLine) {
        try {
            byte[] monoData;
            while ((monoData = convertForData(inputStream)) != null) {
                byte[] stereo = convertLocationalToStereo(monoData);
                dataLine.write(stereo, 0, stereo.length);
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    private byte[] convertLocationalToStereo(byte[] monoData) {
        updatePosition();
        float distance = (float) position.distanceTo(player.getPositionVector());
        float fadeDistance = 10F;
        float maxDistance = 20F;
        float percentage = 1F;
        if (distance > fadeDistance) {
            percentage = 1F - Math.min((distance - fadeDistance) / (maxDistance - fadeDistance), 1F);
        }
        Pair<Float, Float> stereoVolume = getStereoVolume(player.getPositionVector(), player.rotationYaw, position, maxDistance);
        return adjustVolumeStereo(monoData, percentage * stereoVolume.getLeft(), percentage * stereoVolume.getRight());
    }

    private byte[] convertForData(AudioInputStream in) {
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

    public void setPosition(Vec3d position) {
        this.position = position;
    }

    public void updatePosition() {
        if (this.rawName != null)
            NPCHandler.find(rawName).ifPresent(this::setPosition);
    }
}
