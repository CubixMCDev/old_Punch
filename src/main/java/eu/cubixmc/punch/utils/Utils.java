package eu.cubixmc.punch.utils;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Utils {

    /**
     * Load Location from Config
     * @param path path to your Location
     * @param main Main of your plugin
     * @return The location from the config
     */
    public static Location loadLocation(String path, JavaPlugin main){
        Location loc = new Location(Bukkit.getWorld(main.getConfig().getString(path+".world")),
                main.getConfig().getDouble(path+".x"),
                main.getConfig().getDouble(path+".y"),
                main.getConfig().getDouble(path+".z"));

        if(main.getConfig().contains(path+".yaw"))
            loc.setYaw((float) main.getConfig().getDouble(path+".yaw"));

        if(main.getConfig().contains(path+".pitch"))
            loc.setPitch((float) main.getConfig().getDouble(path+".pitch"));

        return loc;
    }

    /**
     * Move Player to another server
     * @param player The player you want to move
     * @param server The server name
     * @param main  Main of your plugin
     */
    public static void movePlayer(Player player, String server, JavaPlugin main) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);
        player.sendPluginMessage(main, "BungeeCord", out.toByteArray());
    }

    /**
     * Send title to player
     * @param player Player to send title
     * @param title The title string
     * @param fadeIn Fade in time
     * @param time Time that the title will stay
     * @param fadeOut Fade out time
     */
    public static void sendTitle(Player player, String title, int fadeIn, int time, int fadeOut) {
        IChatBaseComponent chatTitle = IChatBaseComponent.ChatSerializer.a("{\"text\":\""+title+"\"}");

        PacketPlayOutTitle t = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, chatTitle);
        PacketPlayOutTitle length = new PacketPlayOutTitle(fadeIn,time,fadeOut);

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(t);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(length);
    }

    /**
     * Send title to player
     * @param player Player to send title
     * @param title The title string
     * @param subtitle The subtitle string
     * @param fadeIn Fade in time
     * @param time Time that the title will stay
     * @param fadeOut Fade out time
     */
    public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int time, int fadeOut) {
        IChatBaseComponent chatTitle = IChatBaseComponent.ChatSerializer.a("{\"text\":\""+title+"\"}");
        IChatBaseComponent chatSubtitle = IChatBaseComponent.ChatSerializer.a("{\"text\":\""+subtitle+"\"}");

        PacketPlayOutTitle t = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, chatTitle);
        PacketPlayOutTitle sub = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, chatSubtitle);
        PacketPlayOutTitle length = new PacketPlayOutTitle(fadeIn,time,fadeOut);

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(t);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(sub);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(length);
    }
}
