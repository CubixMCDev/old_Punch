package eu.cubixmc.punch.listenner;

import eu.cubixmc.punch.Punch;
import eu.cubixmc.punch.object.Game;
import eu.cubixmc.punch.object.Puncher;
import eu.cubixmc.punch.utils.Utils;
import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

public class PlayerListenner implements Listener {

    private Punch main;

    public PlayerListenner(Punch punch) {
        this.main = punch;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        main.getScoreboardManager().onLogin(player);
        if(main.getGame().getState() == Game.State.WAITING){
            main.getGame().setWaitingInventory(player);
            event.setJoinMessage(ChatColor.YELLOW+player.getName()+" a rejoint le serveur ! ("+ Bukkit.getOnlinePlayers()+"/"+Bukkit.getMaxPlayers()+")");
        }else{
            main.getGame().setEndingInventory(player);
            player.teleport(main.getGame().getRandomSpawn());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        main.getScoreboardManager().onLogout(player);

        if(main.getGame().getState() == Game.State.PLAYING){
            if(main.getGame().containsPlayer(player))
                main.getGame().removePuncher(main.getGame().getPuncher(player));
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event){
        if(!(event.getEntity() instanceof Player)) return;

        if(event.getCause() == EntityDamageEvent.DamageCause.FALL) event.setCancelled(true);
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event){
        if(event.getEntity() instanceof Player) {
            Vector kb = event.getDamager().getLocation().toVector().subtract(event.getEntity().getLocation().toVector()).multiply(-1);
            Player victim = (Player) event.getEntity();
            if(!main.getGame().containsPlayer(victim)){
                event.setCancelled(true);
                return;
            }
            event.setDamage(0);
            if (event.getDamager() instanceof Player) {
                Player damager = (Player) event.getDamager();
                if(!main.getGame().containsPlayer(damager)){
                    event.setCancelled(true);
                    return;
                }
                double strenght = main.getGame().getMinStrenght();
                if(main.getGame().getPuncher(damager).isStrenghtActive()){
                    strenght += ((main.getGame().getMaxStrenght()-strenght)*(main.getGame().getPuncher(damager).getStrenght())/100);
                }
                kb.multiply(strenght);
                victim.setVelocity(kb);
                victim.setHealth(victim.getMaxHealth());
                main.getGame().getPuncher(victim).punch(main.getGame().getPuncher(damager));
                return;
            } else if (event.getDamager() instanceof Arrow) {
                Arrow arrow = (Arrow) event.getDamager();
                if(arrow.getShooter() instanceof Player){
                    Player damage = (Player) arrow.getShooter();
                    if(damage.getName().equals(victim.getName()) || !main.getGame().containsPlayer(damage)){
                        event.setCancelled(true);
                        return;
                    }
                    double strenght = main.getGame().getMinStrenght();
                    if(main.getGame().getPuncher(damage).isStrenghtActive()){
                        strenght += ((main.getGame().getMaxStrenght()-strenght)*(main.getGame().getPuncher(damage).getStrenght())/100);
                    }
                    kb.multiply(strenght);

                    victim.setVelocity(kb);
                    victim.setHealth(victim.getMaxHealth());
                    main.getGame().getPuncher(victim).punch(main.getGame().getPuncher(damage));
                    return;
                }
            }
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void interact(PlayerInteractEvent event){
        if(event.getItem() == null) return;
        if(!event.getItem().hasItemMeta()) return;
        if(!event.getItem().getItemMeta().hasDisplayName()) return;
        if(event.getAction() != Action.RIGHT_CLICK_AIR) return;

        if(event.getItem().getItemMeta().getDisplayName().equals(ChatColor.RED+"Quitter")){
            Utils.movePlayer(event.getPlayer(),"hub1", main);
        }else{
            if(main.getGame().getState() == Game.State.PLAYING)
                if(main.getGame().containsPlayer(event.getPlayer()))
                    if(event.getItem().getItemMeta().getDisplayName().equals(ChatColor.GOLD+"Activer force")){
                        Puncher puncher = main.getGame().getPuncher(event.getPlayer());
                        if(puncher.getStrenght() > 0 && !puncher.isStrenghtActive()) {
                            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.LEVEL_UP,10,1);
                            Utils.sendTitle(event.getPlayer(),ChatColor.RED+"Boost de force activer",ChatColor.YELLOW+"pendant 10 secondes",5,20,5);
                            main.getGame().getPuncher(event.getPlayer()).activeStrenght();
                            event.getPlayer().setExp(1);
                        }
                    }
        }

        if(event.getItem().getType() != Material.WRITTEN_BOOK)
            event.setCancelled(true);

    }

    @EventHandler
    public void food(FoodLevelChangeEvent event) {
        if(!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        event.setCancelled(true);
        player.setFoodLevel(20);
    }

}
