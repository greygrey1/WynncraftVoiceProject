package com.wynnvp.wynncraftvp.sound;

import com.wynnvp.wynncraftvp.ModCore;
import com.wynnvp.wynncraftvp.config.ConfigHandler;
import com.wynnvp.wynncraftvp.npc.NPCHandler;
import com.wynnvp.wynncraftvp.npc.QuestMarkHandler;
import com.wynnvp.wynncraftvp.sound.custom.CSoundThread;
import com.wynnvp.wynncraftvp.sound.custom.SoundController;
import com.wynnvp.wynncraftvp.sound.line.LineData;
import com.wynnvp.wynncraftvp.sound.line.LineReporter;
import com.wynnvp.wynncraftvp.utils.Utils;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.wynnvp.wynncraftvp.sound.custom.SoundController.loadMono;
import static com.wynnvp.wynncraftvp.sound.custom.SoundController.loadStereo;
import static com.wynnvp.wynncraftvp.utils.Utils.minecraft;
import static com.wynnvp.wynncraftvp.utils.Utils.sendClientChatMessage;

public class SoundPlayer {

    private final List<String> latestSoundPlayed = new ArrayList<>();
    private final LineReporter lineReporter;

    public SoundPlayer() {
        lineReporter = new LineReporter();
    }

    //Code that is run to play all the sounds
    public void playSound(LineData lineData) {
        String line = lineData.getSoundLine();
        SoundsHandler soundsHandler = ModCore.instance.soundsHandler;
        if (!soundsHandler.get(line).isPresent()) {
           // System.out.println("Does not contain line: " + lineData.getRealLine());
            lineReporter.missingLine(lineData);
            return;
        }
        if (isOnCoolDown(line)) {
            //System.out.println("Sound: " + line + " is on cooldown.");
            return;
        }

        if (minecraft().player == null) {
            return;
        }

        if (minecraft().world == null) {
            return;
        }

        //System.out.println("Playing sound: " + line);
        //Minecraft.getMinecraft().getSoundHandler().stopSounds();
        soundsHandler.get(line).ifPresent(sound -> {
            final CustomSoundClass customSoundClass = sound.getCustomSoundClass();
            final File audioFile = new File(Utils.FILE_ROOT, getQuest(sound.getId())+"/"+sound.getId()+".ogg");
            if (!audioFile.exists()) {
                return;
            }
            //Solves ArmorStand problem with ??? as name
            //WARNING: not yet tested
            QuestMarkHandler.put(getQuest(sound.getId()));

            SoundController.cSoundThreads.forEach(cSoundThread -> cSoundThread.setStopped(true));
            SoundController.cSoundThreads.removeIf(CSoundThread::isStopped);

            //If this is a moving sound or it is set to play all sounds on player
            if (customSoundClass.isMovingSound() || ConfigHandler.playAllSoundsOnPlayer) {
                loadMono(audioFile).start();
                addSoundToCoolDown(line);
                return;
            }
            String rawName = getRawName(sound.getId());
            if (NPCHandler.getNamesHandlers().containsKey(rawName)) {
                NPCHandler.find(rawName).ifPresent(vector -> {
                    if (Minecraft.getMinecraft().player.getDistance(vector.x, vector.y, vector.z) >= 20) {
                        loadStereo(audioFile, Minecraft.getMinecraft().player.getPositionVector()).start();
                    } else {
                        loadStereo(audioFile, rawName).start();
                    }
                });
            } else {
                loadStereo(audioFile, Minecraft.getMinecraft().player.getPositionVector()).start();
            }
            addSoundToCoolDown(line);
        });
    }

    /*private void playSoundAtCoords(Vec3d blockPos, SoundEvent soundEvent) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        player.getEntityWorld().playSound(blockPos.x, blockPos.y, blockPos.z, soundEvent, SoundCategory.VOICE, ConfigHandler.blockCutOff / 16f, 1, false);
    }*/

    private String getQuest(String id) {
        String result = "none";
        if (id.contains("-")) {
            String[] args = id.split("-");
            result = args[0];
        }
        return result;
    }

    private String getRawName(String name) {
        return ModCore.instance.soundsHandler.findNPCName(name);
    }

    private void addSoundToCoolDown(String soundName) {
        if (latestSoundPlayed.size() >= ConfigHandler.maxCoolDownLines) {
            latestSoundPlayed.remove(0);
        }
        latestSoundPlayed.add(soundName);
    }

    private boolean isOnCoolDown(String soundName) {
        return latestSoundPlayed.contains(soundName);
    }

    public void clearCoolDown() {
        latestSoundPlayed.clear();
    }

}
