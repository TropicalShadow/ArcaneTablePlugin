package me.tropicalshadow.arcanetable;

import me.tropicalshadow.arcanetable.gui.BaseGui;
import me.tropicalshadow.arcanetable.gui.TableGui;
import me.tropicalshadow.arcanetable.listener.BlockListener;
import me.tropicalshadow.arcanetable.utils.Logging;
import me.tropicalshadow.arcanetable.utils.PluginExtensionsUtils;
import me.tropicalshadow.arcanetable.utils.VersionUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;

public final class ArcaneTable extends JavaPlugin {

    private static ArcaneTable INSTANCE;
    public static Material ETABLEMATERIAL;
    public static Object ADVANCEMENT;
    public YamlConfiguration langConfig;


    @Override
    public void onEnable() {
        INSTANCE = this;
        VersionUtils.versionControl();
        new PluginExtensionsUtils();
        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdir();
            }
            this.saveDefaultConfig();
            reloadConfig();
            langConfig();
        }catch(Exception e) {e.printStackTrace();}

        addListener(new BlockListener());

        Logging.info("Plugin Enabled");
    }
    @Override
    public void onDisable() {
        timber();
        HandlerList.unregisterAll(this);
        Logging.info("Plugin Disabled");
        INSTANCE = null;
    }
    public static void timber(){
        BaseGui.GUI_INVETORIES.forEach((inv,gui)->{
            if(gui instanceof TableGui){
                TableGui enchantmentTable = ((TableGui) gui);
                ItemStack item = enchantmentTable.getCurrentItem();
                if(!(item == null || item.getType().equals(Material.AIR))) {
                    if(inv.getViewers().size()>=1){
                        Player player = (Player) inv.getViewers().get(0);
                        Map<Integer, ItemStack> leftover = player.getInventory().addItem(item);
                        leftover.values().forEach((itemStack -> player.getWorld().dropItem(player.getLocation(),itemStack)));
                        player.closeInventory();
                    }
                }
            }
        });
    }

    private void addListener(Listener... listeners){
        PluginManager pm = getServer().getPluginManager();
        for (Listener listener : listeners) {
            pm.registerEvents(listener, this);
        }
    }

    public void langConfig(){
        File file = new File(this.getDataFolder(), "lang.yml");
        if(!file.exists())this.saveResource("lang.yml",false);
        langConfig = YamlConfiguration.loadConfiguration(file);
    }

    public static Material getEnchantingTableMaterial(){
        return ETABLEMATERIAL;
    }

    public static ArcaneTable getPlugin() {
        return INSTANCE;
    }
}
