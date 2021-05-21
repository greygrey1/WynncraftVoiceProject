package com.wynnvp.wynncraftvp.sound;

import com.wynnvp.wynncraftvp.ModCore;
import com.wynnvp.wynncraftvp.events.SoundCooldown;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class SoundPlayer {
    private int delay = 60;
    private ArrayList<String> soundsOnCoolDown;

    public SoundPlayer(){
        soundsOnCoolDown = new ArrayList<>();
    }

    //Code that is run to play all the sounds
    public void playSound(String line) {
        ModCore modCore = ModCore.instance;
        SoundsHandler soundsHandler = modCore.soundsHandler;
        line = SoundsHandler.formatToSound(line);
        if (!soundsHandler.sounds.containsKey(line)) {
            System.out.println("Does not contain line: " + line);
            return;
        }
        if (SoundCooldown.isOnCoolDown(line)){
            System.out.println("Sound: " + line + " is on cooldown.");
            return;
        }

        System.out.println("Playing sound: " + line);
        Minecraft.getMinecraft().getSoundHandler().stopSounds();
        CustomSoundClass customSoundClass = soundsHandler.sounds.get(line);
        SoundEvent soundEvent = customSoundClass.getSoundEvent();

        //If this is a moving sound
        if (customSoundClass.isMovingSound()) {
            //Play the sound at the player
            Minecraft.getMinecraft().getSoundHandler().playSound(new SoundAtPlayer(soundEvent));
            SoundCooldown.addSoundToCoolDown(line);
            return;
        }
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        playSoundAtCoords(player.getPosition(), soundEvent);
        SoundCooldown.addSoundToCoolDown(line);
    }


    private void playSoundAtCoords(BlockPos blockPos, SoundEvent soundEvent) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        player.getEntityWorld().playSound(blockPos.getX(), blockPos.getY(), blockPos.getZ(), soundEvent, SoundCategory.VOICE, 1, 1, false);
    }




}
