package com.wynnvp.wynncraftvp.events;


import com.wynnvp.wynncraftvp.ModCore;
import com.wynnvp.wynncraftvp.npc.NPCHandler;
import com.wynnvp.wynncraftvp.npc.QuestMarkHandler;
import com.wynnvp.wynncraftvp.sound.SoundPlayer;
import com.wynnvp.wynncraftvp.sound.custom.CSoundThread;
import com.wynnvp.wynncraftvp.sound.custom.SoundController;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class SendChatMessageEvent {

    @SubscribeEvent
    public static void sendChat(ClientChatEvent event){
        String commandText = event.getMessage();
        if (!commandText.equalsIgnoreCase("/class")) {
            return;
        }
        SoundController.cSoundThreads.forEach(cSoundThread -> cSoundThread.setStopped(true));
        SoundController.cSoundThreads.removeIf(CSoundThread::isStopped);

        ModCore.instance.soundPlayer.clearCoolDown();
        NPCHandler.getNamesHandlers().clear();
        QuestMarkHandler.getWichQuest().clear();
    }
}
