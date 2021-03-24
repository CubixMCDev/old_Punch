package eu.cubixmc.punch.task;

import eu.cubixmc.punch.Punch;
import eu.cubixmc.punch.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class EndingTask extends BukkitRunnable {

    private Punch main;
    private int time;

    public EndingTask(Punch punch, int time){
        this.main = punch;
        this.time = time;
    }

    @Override
    public void run() {
        if(time >= 0){
            if(time == 0){
                for(Player player: Bukkit.getOnlinePlayers())
                    Utils.movePlayer(player,"hub1", main);
            }else{
                main.getGame().sendMessage("Fermeture du serveur dans "+time+" secondes.");
            }
        }else{
            //Bukkit.shutdown();
            this.cancel();
        }
        time--;
    }
}
