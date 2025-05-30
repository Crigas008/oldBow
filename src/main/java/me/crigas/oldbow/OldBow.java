package me.crigas.oldbow;

import me.crigas.oldbow.Listeners.BowShoot;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class OldBow extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        Bukkit.getLogger().info("OldBow plugin enabled!");
        Bukkit.getPluginManager().registerEvents(new BowShoot(), this);
    }


    @Override
    public void onDisable() {
        Bukkit.getLogger().info("OldBow plugin disabled!");


    }
}
