package eu.cubixmc.punch.object;

import eu.cubixmc.punch.Punch;
import eu.cubixmc.punch.task.EndingTask;
import eu.cubixmc.punch.task.PlayingTask;
import eu.cubixmc.punch.task.WaitingTask;
import eu.cubixmc.punch.utils.Utils;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class Game {

    public enum State{
        WAITING, PLAYING, ENDING;
    }

    private Punch main;
    private HashMap<String, Puncher> puncherList;
    private State state;
    private byte playerCount;

    /* RULE */
    private final ArrayList<Location> SPAWNLIST;
    private final Location WAITINGLOC;
    private final int MINPLAYER, MINY, LIFE;
    private final double MINSTRENGHT, MAXSTRENGHT;

    /**
     * Create a new game
     * @param punch Main
     */
    public Game(Punch punch){
        this.main = punch;
        this.puncherList = new HashMap<>();

        /* LOAD RULE */
        Puncher.setStrenghtGain((byte) main.getConfig().getInt("rule.strenght-per-punch"));
        this.MINPLAYER = main.getConfig().getInt("rule.min-players");
        this.MINY = main.getConfig().getInt("rule.min-y");
        this.LIFE = main.getConfig().getInt("rule.life");
        this.WAITINGLOC = Utils.loadLocation("location.waiting", main);
        this.MINSTRENGHT = main.getConfig().getDouble("rule.min-strenght");
        this.MAXSTRENGHT = main.getConfig().getDouble("rule.max-strenght");
        //Load spawnlist
        this.SPAWNLIST = new ArrayList<>();
        for(String key: main.getConfig().getConfigurationSection("location.spawn").getKeys(false))
            this.SPAWNLIST.add(Utils.loadLocation("location.spawn."+key,main));

        setWaiting();
    }

    public void setWaiting(){
        this.state = State.WAITING;

        for(Player player: Bukkit.getOnlinePlayers()){
            setWaitingInventory(player);
            player.teleport(WAITINGLOC);
            main.getScoreboardManager().onLogin(player);
        }

        WaitingTask wait = new WaitingTask(this,30);
        wait.runTaskTimer(main,20,20);
    }

    public void setPlaying(){
        for(Player player: Bukkit.getOnlinePlayers()){
            Puncher puncher = new Puncher(player,LIFE);
            puncherList.put(player.getName(),puncher);

            setPlayingInventory(player);
            player.teleport(getRandomSpawn());
        }

        playerCount = (byte) puncherList.size();

        PlayingTask playingTask = new PlayingTask(main);
        playingTask.runTaskTimer(main,20,20);


        this.state = State.PLAYING;
    }

    public void setEnding(){
        this.state = State.ENDING;

        for(Player player: Bukkit.getOnlinePlayers()) {
            setEndingInventory(player);
            Utils.sendTitle(player,ChatColor.GOLD+puncherList.keySet().iterator().next(),ChatColor.YELLOW+"est le grand vainqueur !",1,60,5);
        }

        EndingTask end = new EndingTask(main,10);
        end.runTaskTimer(main,20,20);
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
     * Get puncher from a player
     * @param player the player
     * @return The puncher link to player
     */
    public Puncher getPuncher(Player player){
        return puncherList.get(player.getName());
    }

    public int getMinY(){
        return MINY;
    }

    public void kill(Puncher victim){
        if(victim.getLastPuncher() == null)
            sendMessage(victim.getPlayer().getName()+" est mort!");
        else
            sendMessage(victim.getPlayer().getName()+" a été tué par "+victim.getLastPuncher().getPlayer().getName()+"!");
        victim.die();
        victim.getPlayer().teleport(getRandomSpawn());
        if(victim.isDead()){
            sendMessage(victim.getPlayer().getName()+" a été éliminé!");
            setEndingInventory(victim.getPlayer());
            removePuncher(victim);
            victim.getPlayer().playSound(victim.getPlayer().getLocation(),Sound.ENDERDRAGON_GROWL,10,1);
        }else {
            main.getGame().setPlayingInventory(victim.getPlayer());
        }
    }

    public void removePuncher(Puncher puncher){
        if(!puncherList.containsKey(puncher.getPlayer().getName())) return;

        puncherList.remove(puncher.getPlayer().getName());

        if(puncherList.size() == 1)
            setEnding();
    }

    /**
     * Get number of player at the beginning
     * @return number of player at the beginning
     */
    public byte getPlayerCount(){
        return playerCount;
    }

    /**
     * Check if player is in the game
     * @param player The player
     * @return if player is in the game
     */
    public boolean containsPlayer(Player player){
        return puncherList.containsKey(player.getName());
    }

    /**
     * Get random spawn location
     * @return random spawn location
     */
    public Location getRandomSpawn(){
        Random r = new Random();
        return this.SPAWNLIST.get(r.nextInt(SPAWNLIST.size()));
    }

    public double getMinStrenght() {
        return MINSTRENGHT;
    }

    public double getMaxStrenght() {
        return MAXSTRENGHT;
    }

    public void sendMessage(String message){
        for(Player player: Bukkit.getOnlinePlayers())
            player.sendMessage(ChatColor.YELLOW+"CubixMC "+ChatColor.GOLD+"» "+ChatColor.GRAY+message);
    }

    /**
     * Set waiting inventory to a player
     * @param player
     */
    public void setWaitingInventory(Player player){
        player.getInventory().clear();
        player.setMaxHealth(20);
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.setExp(0);
        player.setLevel(0);
        player.setAllowFlight(false);
        player.setGameMode(GameMode.ADVENTURE);

        //Règlement
        ItemStack rule = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta ruleM = (BookMeta) rule.getItemMeta();
        ruleM.setDisplayName(ChatColor.GOLD+"Règlement");
        ruleM.setLore(Arrays.asList(ChatColor.YELLOW+"Clic droit"));
        ruleM.addPage("Dans Punch, vous devez utiliser vos armes Knockback pour faire sortir vos adversaires de l'arène!"+
                " Il y a deux armes:              "+ChatColor.BOLD+"Bâton"+ChatColor.RESET+": combattez au corps à corps avec ce bâton!"+
                ChatColor.BOLD+"Arc"+ChatColor.RESET+": Faites attention à ces flèches, elles sont puissantes!");
        rule.setItemMeta(ruleM);

        player.getInventory().setItem(0,rule);

        //Quit
        ItemStack quit = new ItemStack(Material.BED);
        ItemMeta quitM = quit.getItemMeta();
        quitM.setDisplayName(ChatColor.RED+"Quitter");
        quitM.setLore(Arrays.asList(ChatColor.YELLOW+"Clic droit"));
        quit.setItemMeta(quitM);

        player.getInventory().setItem(8,quit);

        player.updateInventory();
    }

    /**
     * Set playing inventory to a player
     * @param player
     */
    public void setPlayingInventory(Player player){
        player.getInventory().clear();
        player.setMaxHealth(getPuncher(player).getLife()*2);
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);

        //Bâton
        ItemStack stick = new ItemStack(Material.STICK);
        ItemMeta stickM = stick.getItemMeta();
        stickM.addEnchant(Enchantment.DURABILITY,1,true);
        stickM.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        stickM.setDisplayName(ChatColor.GOLD+"Activer force");
        stickM.setLore(Arrays.asList(ChatColor.YELLOW+"Clic droit"));

        stick.setItemMeta(stickM);
        player.getInventory().setItem(0,stick);

        //Arc
        ItemStack bow = new ItemStack(Material.BOW);
        ItemMeta bowM = bow.getItemMeta();
        bowM.addEnchant(Enchantment.ARROW_INFINITE,1,true);
        bowM.spigot().setUnbreakable(true);
        bowM.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        bowM.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

        bow.setItemMeta(bowM);
        player.getInventory().setItem(1,bow);

        //arrow
        player.getInventory().setItem(17,new ItemStack(Material.ARROW));

        player.updateInventory();
    }

    public void setEndingInventory(Player player){
        player.getInventory().clear();
        player.setMaxHealth(20);
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.setExp(0);
        player.setLevel(0);
        player.setGameMode(GameMode.SPECTATOR);

        //Quit
        ItemStack quit = new ItemStack(Material.BED);
        ItemMeta quitM = quit.getItemMeta();
        quitM.setDisplayName(ChatColor.RED+"Quitter");
        quitM.setLore(Arrays.asList(ChatColor.YELLOW+"Clic droit"));
        quit.setItemMeta(quitM);

        player.getInventory().setItem(8,quit);

        player.updateInventory();
    }


}
