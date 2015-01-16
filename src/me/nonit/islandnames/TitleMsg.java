package me.nonit.islandnames;

import net.minecraft.server.v1_8_R1.ChatSerializer;
import net.minecraft.server.v1_8_R1.EnumTitleAction;
import net.minecraft.server.v1_8_R1.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R1.PlayerConnection;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class TitleMsg
{
    private static final int FADE_IN = 8;
    private static final int FADE_OUT = 8;
    private static final int TIME = 60;

    public static void send( Player player, String title, String subtitle )
    {
        PlayerConnection craftPlayer = ((CraftPlayer )player).getHandle().playerConnection;

        PacketPlayOutTitle titleBig = new PacketPlayOutTitle( EnumTitleAction.TITLE, ChatSerializer.a( "{'text': ''}" ).a(title), FADE_IN, TIME, FADE_OUT );
        PacketPlayOutTitle titleSmall = new PacketPlayOutTitle( EnumTitleAction.SUBTITLE, ChatSerializer.a("{'text': ''}").a(subtitle), FADE_IN, TIME, FADE_OUT );

        craftPlayer.sendPacket(titleBig);
        craftPlayer.sendPacket(titleSmall);
    }
}