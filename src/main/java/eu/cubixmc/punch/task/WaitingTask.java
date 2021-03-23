package eu.cubixmc.punch.task;

import eu.cubixmc.punch.object.Game;
import eu.cubixmc.punch.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class WaitingTask extends BukkitRunnable {

    private final Game game;
    private int timer;
    private final int TIMESTART;

    public WaitingTask(Game game, int timeBeforeStart){
        this.game = game;
        this.timer = timeBeforeStart;
        this.TIMESTART = timeBeforeStart;
    }

    @Override
    public void run() {
        if(Bukkit.getOnlinePlayers().size() < game.getMinPlayer())
            timer = TIMESTART;

        else if(timer <= 10){
            if(timer == 0){
                game.setPlaying();
                this.cancel();
            }else
                for(Player player: Bukkit.getOnlinePlayers())
                    Utils.sendTitle(player, ChatColor.RED+""+timer, ChatColor.YELLOW+"Préparez vous",0,20,10);

        }
        timer--;
    }
}
