package com.wynnvp.wynncraftvp.sound.custom.thread;

import com.wynnvp.wynncraftvp.npc.NPCHandler;
import com.wynnvp.wynncraftvp.sound.custom.CSoundThread;
import com.wynnvp.wynncraftvp.sound.custom.SoundController;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.tuple.Pair;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

import static com.wynnvp.wynncraftvp.utils.Utils.*;
import static javax.sound.sampled.AudioSystem.getAudioInputStream;

public class StereoThread extends CSoundThread {

    private final EntityPlayerSP player;
    private final File file;
    private final String rawName;

    private Vec3d position;

    public StereoThread(Vec3d position, File file) {
        setDaemon(true);
        setName("VoW - Stereo Audio");
        this.position = position;
        this.player = player();
        this.file = file;
        this.rawName = null;
        this.stopped = false;
    }

    public StereoThread(String rawName, File file) {
        setDaemon(true);
        setName("VoW - Stereo Audio");
        this.rawName = rawName;
        NPCHandler.find(rawName).ifPresent(this::setPosition);
        this.player = player();
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
                }
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        } finally {
            SoundController.cSoundThreads.remove(this);
        }
    }

    private void stream(AudioInputStream inputStream, SourceDataLine dataLine) {
        readWrite(inputStream, (audioData, volume) -> {
            byte[] stereo = convertLocationalToStereo(audioData, volume);
            if (stereo == null) {
                stopped = true;
                return;
            }
            dataLine.write(stereo, 0, stereo.length);
        });
    }

    private byte[] convertLocationalToStereo(byte[] monoData, float volume) {
        if (monoData == null) return null;
        updatePosition();
        float distance = (float) position.distanceTo(player.getPositionVector());
        float fadeDistance = 10F;
        float maxDistance = 20F;
        float percentage = 1F;
        if (distance > fadeDistance) {
            percentage = 1F - Math.min((distance - fadeDistance) / (maxDistance - fadeDistance), 1F);
        }
        Pair<Float, Float> stereoVolume = getStereoVolume(player.getPositionVector(), player.rotationYaw, position, maxDistance);
        return adjustVolumeStereo(monoData, volume * percentage * stereoVolume.getLeft(), volume * percentage * stereoVolume.getRight());
    }

    public void setPosition(Vec3d position) {
        this.position = position;
    }

    public void updatePosition() {
        if (this.rawName != null)
            NPCHandler.find(rawName).ifPresent(this::setPosition);
    }
}
