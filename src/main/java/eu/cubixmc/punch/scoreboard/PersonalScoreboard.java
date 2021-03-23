package eu.cubixmc.punch.scoreboard;

import eu.cubixmc.punch.Punch;
import eu.cubixmc.punch.object.Game;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class PersonalScoreboard {
    private final Player player;
    private Punch main;
    private final UUID uuid;
    private final ObjectiveSign objectiveSign;
    final Date date = new Date();
    private String currentDate = new SimpleDateFormat("dd-MM-yyyy").format(date).replace("-", "/");

    PersonalScoreboard(Punch main, Player player){
        this.main = main;
        this.player = player;
        uuid = player.getUniqueId();
        objectiveSign = new ObjectiveSign("sidebar", "DeACoudre");

        reloadData();
        objectiveSign.addReceiver(player);
    }

    public void reloadData(){}

    public void setLines(String ip){
        objectiveSign.setDisplayName(ChatColor.DARK_GRAY+"- "+ChatColor.GOLD+"Punch"+ChatColor.DARK_GRAY+" -");

        objectiveSign.setLine(0, "§8» §7" + currentDate);

        if(main.getGame().getState() == Game.State.WAITING) {
            objectiveSign.setLine(1, "§1");
            objectiveSign.setLine(2, "§8» §7Joueurs: §e" + Bukkit.getOnlinePlayers().size() + "§6/§e" + Bukkit.getMaxPlayers());
            objectiveSign.setLine(3, "§8» §7Attente de joueurs...");
            objectiveSign.setLine(4, "§2");
            objectiveSign.setLine(5, "§8» " + ip);
        }else if(main.getGame().getState() == Game.State.PLAYING){
            /*if(jumper == null)
                jumper = main.getGame().getJumper(player);


            objectiveSign.setLine(1, "§1");
            objectiveSign.setLine(2, "§8» §7Joueurs: §e" + main.getGame().getJumperList().size() + "§6/§e" + main.getGame().getJumperColorList().size());
            objectiveSign.setLine(3, "§8» §7Joueur qui saute: §e" + main.getGame().getCurrentJumper().getPlayer().getName());
            if(jumper != null) {
                objectiveSign.setLine(4, "§c");
                objectiveSign.setLine(5, "§8» §7Vies: §e" + jumper.getLife());
            }
            objectiveSign.setLine(6,"§8» §7Round: §e"+main.getGame().getRound());
            objectiveSign.setLine(7, "§2");
            objectiveSign.setLine(8, "§8» " + ip);
        }else if(main.getGame().getState() == Game.State.ENDING){
            objectiveSign.setLine(1, "§1");
            objectiveSign.setLine(2, "§8» §7Gagnant: §e" + main.getGame().getJumperList().get(0).getPlayer().getName());
            objectiveSign.setLine(3, "§8» §7Partie terminé...");
            objectiveSign.setLine(4, "§2");
            objectiveSign.setLine(5, "§8» " + ip);*/
        }

        objectiveSign.updateLines();
    }

    public void onLogout(){
        objectiveSign.removeReceiver(Bukkit.getServer().getOfflinePlayer(uuid));
    }
}