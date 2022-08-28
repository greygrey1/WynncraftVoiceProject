package com.wynnvp.wynncraftvp.events;

import com.wynnvp.wynncraftvp.ModCore;
import com.wynnvp.wynncraftvp.sound.custom.SoundController;
import com.wynnvp.wynncraftvp.sound.custom.thread.StereoThread;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class SoundTickEvent {

    @SubscribeEvent
    public void onTickEvent(TickEvent.ClientTickEvent event) {
        if (!ModCore.inServer) return;
        if (SoundController.cSoundThreads.containsKey(SoundController.SoundType.STEREO)) {
            StereoThread thread = (StereoThread) SoundController.cSoundThreads.get(SoundController.SoundType.STEREO);
            thread.updatePosition();
            SoundController.cSoundThreads.put(SoundController.SoundType.STEREO, thread);
        }
    }

}
