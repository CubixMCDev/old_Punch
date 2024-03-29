package eu.cubixmc.punch;

import eu.cubixmc.punch.listenner.PlayerListenner;
import eu.cubixmc.punch.object.Game;
import eu.cubixmc.punch.scoreboard.ScoreboardManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Punch extends JavaPlugin {
    private ScoreboardManager scoreboardManager;

    private ScheduledExecutorService executorMonoThread;
    private ScheduledExecutorService scheduledExecutorService;
    private Game game;

    @Override
    public void onEnable() {
        scheduledExecutorService = Executors.newScheduledThreadPool(16);
        executorMonoThread = Executors.newScheduledThreadPool(1);
        scoreboardManager = new ScoreboardManager(this);

        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new PlayerListenner(this),this);
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        game = new Game(this);
    }

    @Override
    public void onDisable() {
        getScoreboardManager().onDisable();
    }

    public Game getGame(){
        return game;
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public ScheduledExecutorService getExecutorMonoThread() {
        return executorMonoThread;
    }

    public ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }
}
