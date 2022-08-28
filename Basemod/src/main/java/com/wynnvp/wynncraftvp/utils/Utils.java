package com.wynnvp.wynncraftvp.utils;

import com.wynnvp.wynncraftvp.ModCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.security.MessageDigest;

public class Utils {

    public static final File FILE_ROOT = new File(Minecraft.getMinecraft().gameDir, ModCore.MODID);

    //Checks if the players is even on wynn
    public static boolean inWynn() {
        try {
            return Minecraft.getMinecraft().getCurrentServerData().serverIP.toLowerCase().contains("wynncraft");
        } catch (NullPointerException e) {
            return false;
        }
    }

    //Returns the path to the sound file in the format that is used in the JSON
    public static String pathToFile(String firstNumber, String secondNumber, String npcName, String line) {
        if (line.contains(Minecraft.getMinecraft().player.getName())) {
            line = line.replaceAll(Minecraft.getMinecraft().player.getName(), "player");
        }
        npcName = npcName.replaceAll(" ", "");

        line.replaceAll(" ", "");

        return npcName + "." + secondNumber + "." + firstNumber + line;
    }

    public static String sha256(final String base) {
        try{
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            final byte[] hash = digest.digest(base.getBytes("UTF-8"));
            final StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < hash.length; i++) {
                final String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public static short bytesToShort(byte b1, byte b2) {
        return (short) (((b2 & 0xFF) << 8) | (b1 & 0xFF));
    }

    public static byte[] adjustVolumeStereo(byte[] audio, float volumeLeft, float volumeRight) {
        for (int i = 0; i < audio.length; i += 2) {
            short audioSample = bytesToShort(audio[i], audio[i + 1]);
            audioSample = (short) (audioSample * (i % 4 == 0 ? volumeLeft : volumeRight));
            audio[i] = (byte) audioSample;
            audio[i + 1] = (byte) (audioSample >> 8);
        }
        return audio;
    }

    public static Pair<Float, Float> getStereoVolume(Vec3d playerPos, float yaw, Vec3d soundPos, double voiceChatDistance) {
        Vec3d d = soundPos.subtract(playerPos).normalize();
        Vec2f diff = new Vec2f((float) d.x, (float) d.z);
        float diffAngle = angle(diff, new Vec2f(-1F, 0F));
        float dif = (float) (Math.abs(playerPos.y - soundPos.y) / voiceChatDistance);

        float angle = normalizeAngle(diffAngle - (yaw % 360F));

        float rot = angle / 180F;
        float perc = rot;
        if (rot < -0.5F) {
            perc = -(0.5F + (rot + 0.5F));
        } else if (rot > 0.5F) {
            perc = 0.5F - (rot - 0.5F);
        }

        perc = perc * (1 - dif);

        float left = perc < 0F ? Math.abs(perc * 1.4F) + 0.3F : 0.3F;
        float right = perc >= 0F ? (perc * 1.4F) + 0.3F : 0.3F;

        float fill = 1F - Math.max(left, right);
        left += fill;
        right += fill;
        return new ImmutablePair<>(left, right);
    }

    private static float normalizeAngle(float angle) {
        angle = angle % 360F;
        if (angle <= -180F) {
            angle += 360F;
        } else if (angle > 180F) {
            angle -= 360F;
        }
        return angle;
    }

    private static float angle(Vec2f vec1, Vec2f vec2) {
        return (float) Math.toDegrees(Math.atan2(vec1.x * vec2.x + vec1.y * vec2.y, vec1.x * vec2.y - vec1.y * vec2.x));
    }

}
