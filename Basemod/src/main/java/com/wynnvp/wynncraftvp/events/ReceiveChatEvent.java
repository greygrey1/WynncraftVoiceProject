package com.wynnvp.wynncraftvp.events;

import com.wynnvp.wynncraftvp.ModCore;
import com.wynnvp.wynncraftvp.sound.line.LineData;
import com.wynnvp.wynncraftvp.utils.LineFormatter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.vecmath.Vector3f;

import static com.wynnvp.wynncraftvp.utils.Utils.player;

@Mod.EventBusSubscriber
public class
ReceiveChatEvent {

    private static final Vector3f mixedFeelingsNPC1 = new Vector3f(-5881, 17, -2464);
    private static final Vector3f mixedFeelingsNPC2 = new Vector3f(-5835, 16, -2463);
    private static final Vector3f mixedFeelingsNPC3 = new Vector3f(-5807, 16, -2421);

    public static boolean stopMod = false;

    @SubscribeEvent
    public static void receivedChat(ClientChatReceivedEvent event) {
        if (stopMod) return;
        String msg = event.getMessage().getUnformattedText();

        //Replace player Name with "soldier"
        String name = GetPlayerName(event.getMessage().toString());
        LineData lineData = LineFormatter.formatToLineData(msg.replace(name, "soldier"));

        if (lineData == null) { // invalid line data returned
            return;
        }

        if (isInMixedFeelingsQuest()) {
            String result = getMixedFeelingsLine(lineData.getSoundLine());
            if (result != null) {
                lineData.setSoundLine(result);
            }
        }
        ModCore.instance.soundPlayer.playSound(lineData);
    }


    private static boolean isInMixedFeelingsQuest() {
        EntityPlayerSP player = player();
        return player.getDistance(mixedFeelingsNPC1.x, mixedFeelingsNPC1.y, mixedFeelingsNPC1.z) < 250;

    }

    private static String getMixedFeelingsLine(String msg) {
        EntityPlayerSP player = player();

        if (player.getDistance(mixedFeelingsNPC1.x, mixedFeelingsNPC1.y, mixedFeelingsNPC1.z) < 15) {
            msg = GetRightMixedFeelingsLine("mixedfeelingscorkuscitycitizen1", msg);
        } else if (player.getDistance(mixedFeelingsNPC2.x, mixedFeelingsNPC2.y, mixedFeelingsNPC2.z) < 15) {
            msg = GetRightMixedFeelingsLine("mixedfeelingscorkuscitycitizen2", msg);
        } else if (player.getDistance(mixedFeelingsNPC3.x, mixedFeelingsNPC3.y, mixedFeelingsNPC3.z) < 15) {
            msg = GetRightMixedFeelingsLine("mixedfeelingscorkuscitycitizen3", msg);
        }

        return msg;
    }

    private static String GetRightMixedFeelingsLine(String fileName, String msg) {
        boolean foundMsg = false;

        if (msg.equalsIgnoreCase("2/5corkuscitycitizencorkushasbeenurgingtouristsandenvoysfromtheotherprovincestogainrecognition")) {
            fileName = fileName + "1";
            foundMsg = true;
        } else if (msg.equalsIgnoreCase("2/5corkuscitycitizenyouknowaboutthepatriotsofcorkus?")) {
            fileName = fileName + "2";
            foundMsg = true;
        } else if (msg.equalsIgnoreCase("2/6corkuscitycitizenhmmasithappensihaveseensomestrangethingsaroundhere")) {
            fileName = fileName + "3";
            foundMsg = true;
        }
        if (!foundMsg) return null;


        return fileName;
    }


    private static String GetPlayerName(String eventMessageToString) {
        String segments[] = eventMessageToString.split("hoverEvent=HoverEvent\\{action=SHOW_TEXT, value='TextComponent\\{text='");
        if (segments.length <= 1) return Minecraft.getMinecraft().player.getDisplayNameString();

        String name = segments[segments.length - 1].split("',")[0];
        if (name.contains("Previous")) return Minecraft.getMinecraft().player.getDisplayNameString();
        return name.split("'")[0];
    }
}


