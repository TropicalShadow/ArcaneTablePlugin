package me.tropicalshadow.arcanetable;

import me.tropicalshadow.arcanetable.gui.BaseGui;
import me.tropicalshadow.arcanetable.listener.BlockListener;
import me.tropicalshadow.arcanetable.listener.GuiHook;
import me.tropicalshadow.arcanetable.utils.Logging;
import org.bukkit.Material;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class ArcaneTable extends JavaPlugin {

    private static ArcaneTable INSTANCE;
    private static Material enchantingTableMaterial;


    @Override
    public void onEnable() {
        INSTANCE = this;
        try{
            enchantingTableMaterial = Material.ENCHANTING_TABLE;
        }catch(NoSuchFieldError err){
            enchantingTableMaterial = Material.getMaterial("ENCHANTMENT_TABLE");
        }
        addListener(new BlockListener());

        Logging.info("Plugin Enabled");
    }

    @Override
    public void onDisable() {
        BaseGui.Timber();
        HandlerList.unregisterAll(this);
        Logging.info("Plugin Disabled");
        INSTANCE = null;
    }

    private void addListener(Listener... listeners){
        PluginManager pm = getServer().getPluginManager();
        for (int i = 0; i < listeners.length; i++) {
            pm.registerEvents(listeners[i],this);
        }
    }

    public static Material getEnchantingTableMaterial(){
        return enchantingTableMaterial;
    }

    public static ArcaneTable getPlugin() {
        return INSTANCE;
    }
}
