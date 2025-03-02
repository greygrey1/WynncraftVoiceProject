package com.wynnvp.wynncraftvp.sound;

import com.wynnvp.wynncraftvp.ModCore;
import com.wynnvp.wynncraftvp.sound.line.LineData;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.wynnvp.wynncraftvp.utils.LineFormatter.formatToLineData;

public class SoundsHandler {

    //private final List<SoundObject> sounds;
    private final HashMap<String, SoundObject> sounds;
    private final Set<String> npcNames;

    public SoundsHandler() {
        sounds = new HashMap<>();
        npcNames = new HashSet<>();
        //sounds = new ArrayList<>();
        Sounds.register(this);
    }

    public static SoundEvent registerSound(String name) {
        ResourceLocation location = new ResourceLocation(ModCore.MODID, name);
        SoundEvent event = new SoundEvent(location);
        event.setRegistryName(name);
        ForgeRegistries.SOUND_EVENTS.register(event);
        return event;
    }

    /**
     * Method to add the sounds to the system
     *
     * @param message     Identification message
     * @param id          Identification sound
     * @param movingSound True if the sound moves with the player,
     *                    otherwise it will move with the ArmorStand
     */
    public void addSound(String message, String id, boolean movingSound) {

        addSound(message, id, movingSound, null);
    }

    public void addSound(String message, String id, boolean movingSound, Vector3 position) {

        addSound(message, id, movingSound, position, 0);
    }

    public void addSound(String message, String id, boolean movingSound, int fallOff) {

        addSound(message, id, movingSound, null, 0);
    }

    //If position is 0 null use default. If falloff is 0 use default
    public void addSound(String message, String id, boolean movingSound, Vector3 position, int fallOff) {

        LineData lineData = formatToLineData(message);
        npcNames.add(lineData.getNPCName());
        message = lineData.getSoundLine();
        sounds.put(message, new SoundObject(lineData.getNPCName(), id, new CustomSoundClass(registerSound(id), movingSound), position, fallOff));
    }

    public boolean containsName(String rawName) {
        return npcNames.contains(rawName);
    }

    public Optional<SoundObject> get(String message) {
        return Optional.ofNullable(sounds.get(message));
    }

    public String findNPCName(String id) {
        return sounds.values().stream()
                .filter(soundObject -> soundObject.getId().equalsIgnoreCase(id))
                .map(SoundObject::getNpcName)
                .findAny()
                .orElse("");
    }

    public String getNPCName(String quest) {
        return sounds.entrySet().stream().filter(entry -> entry.getValue().getId().contains("-") &&
                        entry.getValue().getId().contains(quest)
                        && entry.getKey().contains("???:")).map(map ->
                        map.getValue().getNpcName())
                .findAny().orElse(null);
    }


    public static String getNameForMessage(String message) {
        String split = message.split(": ")[0];
        return split.trim().toLowerCase().replaceAll("[^a-zA-Z\\d]", "").replaceAll("\\d", "");
    }

    public static String getNameForId(String name) {
        String id = "???";
        if (name.contains("-")) {
            String[] args = name.split("-");
            id = args[1];
        } else if (name.contains("talkingmushroom")) {
            id = "talkingmushroom";
        }
        return id;
    }


}
