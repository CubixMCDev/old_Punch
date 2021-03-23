package eu.cubixmc.punch.object;

import org.bukkit.entity.Player;

public class Puncher {

    private static byte strenghtGain;

    private Player player;
    private int life;
    private int kill;
    private Puncher lastPunch;
    private byte strenght;

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

        if((puncher.strenght+Puncher.strenghtGain) < 100)
            puncher.strenght += Puncher.strenghtGain;
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

    /**
     * Set strenght gain per punch
     * @param strenght How many strenght player gain when punch player
     */
    public static void setStrenghtGain(byte strenght){
        Puncher.strenghtGain = strenght;
    }


}
