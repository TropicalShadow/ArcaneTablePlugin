package me.tropicalshadow.arcanetable.utils;

import me.tropicalshadow.arcanetable.ArcaneTable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class Logging {

    private static final Component PREFIX = Component.text("[AT] ", NamedTextColor.AQUA);

    public static void info(String obj){
        ArcaneTable.getPlugin().getComponentLogger().info(PREFIX.append(Component.text(obj, NamedTextColor.GREEN)));
    }
    public static void warning(String obj){
        ArcaneTable.getPlugin().getComponentLogger().warn(PREFIX.append(Component.text(obj, NamedTextColor.YELLOW)));
    }
    public static void danger(String obj){
        ArcaneTable.getPlugin().getComponentLogger().error(PREFIX.append(Component.text(obj, NamedTextColor.RED)));
    }
}
