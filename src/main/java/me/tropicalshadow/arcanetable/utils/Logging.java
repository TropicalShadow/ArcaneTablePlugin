package me.tropicalshadow.arcanetable.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Logging {

    private static final String PREFIX = ChatColor.BLUE+"[AT] ";

    public static void info(Object obj){
        Bukkit.getServer().getLogger().info(PREFIX+ChatColor.GREEN.toString()+obj);
    }
    public static void warning(Object obj){
        Bukkit.getServer().getLogger().warning(PREFIX+ChatColor.YELLOW.toString()+obj);
    }
    public static void danger(Object obj){
        Bukkit.getServer().getLogger().info(PREFIX+ChatColor.RED.toString()+obj);
    }
}
