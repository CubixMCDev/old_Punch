package eu.cubixmc.punch.object;

import org.bukkit.entity.Player;

public class Puncher {

    private Player player;
    private int life;
    private int kill;
    private Puncher lastPunch;

    /**
     * Create a puncher
     * @param player    The player
     * @param life      Number of life
     */
    public Puncher(Player player, int life){
        this.player = player;
        this.life = life;
        this.kill = 0;
    }

    /**
     * When player die
     */
    public void die(){
        life--;

        if(lastPunch != null)
            lastPunch.kill++;

        lastPunch = null;
    }

    /**
     * When puncher has been punch
     * @param puncher Who punch
     */
    public void punch(Puncher puncher){
        this.lastPunch = puncher;
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


}
