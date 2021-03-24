package eu.cubixmc.punch.task;

import eu.cubixmc.punch.Punch;
import eu.cubixmc.punch.object.Game;
import eu.cubixmc.punch.object.Puncher;
import eu.cubixmc.punch.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayingTask extends BukkitRunnable {

    private Punch main;

    public PlayingTask(Punch main){
        this.main = main;
    }

    @Override
    public void run() {
        if(main.getGame().getState() != Game.State.PLAYING){
            this.cancel();
            return;
        }

        for(Puncher puncher: main.getGame().getPuncherList()){
            if(puncher.getPlayer().getLocation().getY() <= main.getGame().getMinY()){
                main.getGame().kill(puncher);
            }

            if(puncher.isStrenghtActive()){
                if((System.currentTimeMillis()-puncher.getStartStrenghtTime())/1000>= 10){
                    puncher.disableStrenght();
                    puncher.getPlayer().setExp(0);
                    puncher.getPlayer().setLevel(0);
                    puncher.setStrenght((byte)0);
                    Utils.sendTitle(puncher.getPlayer(), ChatColor.RED+"Boost de force désactiver!",1,20,5);
                }else{
                    if(puncher.getPlayer().getExp() == 0)
                        puncher.getPlayer().setExp(1);
                    else
                        puncher.getPlayer().setExp(0);
                }
            }

        }
    }
}
