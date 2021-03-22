package eu.cubixmc.punch.object;

import eu.cubixmc.punch.Punch;
import eu.cubixmc.punch.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.*;

public class Game {

    public enum State{
        WAITING, PLAYING, ENDING;
    }

    private Punch main;
    private HashMap<Player, Puncher> puncherList;
    private State state;

    /* RULE */
    private final int MINPLAYER;
    private final ArrayList<Location> SPAWNLIST;
    private final Location WAITINGLOC;
    private final int MINY;
    private final int LIFE;

    /**
     * Create a new game
     * @param punch Main
     */
    public Game(Punch punch){
        this.main = punch;
        this.puncherList = new HashMap<>();

        /* LOAD RULE */
        this.MINPLAYER = main.getConfig().getInt("rule.min-players");
        this.MINY = main.getConfig().getInt("rule.min-y");
        this.LIFE = main.getConfig().getInt("rule.life");
        this.WAITINGLOC = Utils.loadLocation("location.waiting", main);
        //Load spawnlist
        this.SPAWNLIST = new ArrayList<>();
        for(String key: main.getConfig().getConfigurationSection("location.spawn").getKeys(false))
            this.SPAWNLIST.add(Utils.loadLocation("location.spawn."+key,main));

        setWaiting();
    }

    public void setWaiting(){
        this.state = State.WAITING;

        for(Player player: Bukkit.getOnlinePlayers())
            setWaitingLocation(player);
    }

    public void setPlaying(){
        this.state = State.PLAYING;
    }

    public void setEnding(){
        this.state = State.ENDING;
    }

    /**
     * Get state of game
     * @return state of game
     */
    public State getState(){
        return state;
    }

    /**
     * Get count for min player
     * @return
     */
    public int getMinPlayer() {
        return MINPLAYER;
    }

    /**
     * Get puncher List
     * @return puncher list
     */
    public Collection<Puncher> getPuncherList(){
        return puncherList.values();
    }

    /**
     * Get random spawn location
     * @return random spawn location
     */
    public Location getRandomSpawn(){
        Random r = new Random();
        return this.SPAWNLIST.get(r.nextInt(SPAWNLIST.size()));
    }

    public void setWaitingLocation(Player player){
        player.getInventory().clear();

        ItemStack rule = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta ruleM = (BookMeta) rule.getItemMeta();
        ruleM.setDisplayName(ChatColor.GOLD+"Règlement");
        ruleM.setLore(Arrays.asList(ChatColor.YELLOW+"Clic droit"));
        ruleM.addPage("Dans Punch, vous devez utiliser vos armes Knockback pour faire sortir vos adversaires de l'arène!"+
                "Il y a deux armes: "+ChatColor.BOLD+"Bâton"+ChatColor.RESET+": combattez au corps à corps avec ce bâton!"+
                ChatColor.BOLD+"Arc"+ChatColor.RESET+": Faites attention à ces flèches, elles sont puissantes!");
        rule.setItemMeta(ruleM);

        player.getInventory().setItem(0,rule);
    }
}
