package eu.cubixmc.punch.object;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class Puncher {

    private static byte strenghtGain;

    private Player player;
    private int life, kill;
    private Puncher lastPunch;
    private byte strenght;
    private long startStrenght;
    private boolean isStrenght;

    /**
     * Create a puncher
     * @param player    The player
     * @param life      Number of life
     */
    public Puncher(Player player, int life){
        this.player = player;
        this.life = life;
        this.kill = 0;
        this.strenght = 0;
    }

    /**
     * When player die
     */
    public void die(){
        life--;

        if(lastPunch != null)
            lastPunch.kill++;

        strenght = 0;
        lastPunch = null;
    }

    /**
     * When puncher has been punch
     * @param puncher Who punch
     */
    public void punch(Puncher puncher){
        this.lastPunch = puncher;

        if(puncher.strenght < 100 && !puncher.isStrenght) {
            puncher.strenght += Puncher.strenghtGain;
            puncher.getPlayer().playSound(puncher.getPlayer().getLocation(), Sound.ORB_PICKUP,10,1);
            if(puncher.strenght > 100)
                puncher.strenght = 100;
            double xp = ((double)puncher.strenght)/100;
            puncher.getPlayer().setExp((float) xp);
            puncher.getPlayer().setLevel(puncher.getStrenght());
        }


    }

    /**
     * Check if player has a life left
     * @return if player has a life left
     */
    public boolean isDead(){
        return life <= 0;
    }

    /**
     * Get player
     * @return player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Get how many kill puncher has done
     * @return  number of kill
     */
    public int getKill() {
        return kill;
    }

    /**
     * Get how many life the player has
     * @return how many life the player has
     */
    public int getLife(){
        return life;
    }

    /**
     * Get strenght of player
     * @return strenght
     */
    public byte getStrenght(){
        return strenght;
    }

    public void setStrenght(byte strenght){
        this.strenght = strenght;
    }

    public void activeStrenght(){
        this.isStrenght = true;
        this.startStrenght = System.currentTimeMillis();
    }

    public void disableStrenght(){
        this.isStrenght = false;
    }

    public long getStartStrenghtTime(){
        return startStrenght;
    }

    public boolean isStrenghtActive(){
        return isStrenght;
    }

    public Puncher getLastPuncher(){
        return lastPunch;
    }

    /**
     * Set strenght gain per punch
     * @param strenght How many strenght player gain when punch player
     */
    public static void setStrenghtGain(byte strenght){
        Puncher.strenghtGain = strenght;
    }


}
