package me.tropicalshadow.arcanetable.utils;

import me.tropicalshadow.arcanetable.ArcaneTable;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

public class VersionUtils {

    public static String version = Bukkit.getVersion();

    public static int versionID = (Integer.parseInt(version.split("\\.")[1]));
    public static boolean isLegacy = false;

    public static void versionControl(){
        Logging.info(version);
        if(version.split("\\.")[1].endsWith(")")){
            isLegacy = false;
        }else {
            isLegacy = versionID < 13;
        }

        try{
            ArcaneTable.ETABLEMATERIAL = Material.ENCHANTING_TABLE;
        }catch(NoSuchFieldError err){
            ArcaneTable.ETABLEMATERIAL = Material.getMaterial("ENCHANTMENT_TABLE");
        }
        try{
            ArcaneTable.ADVANCEMENT = ArcaneTable.getPlugin().getServer().getAdvancement(NamespacedKey.minecraft("story/enchant_item"));
        }catch (Exception e){
            e.printStackTrace();
            ArcaneTable.ADVANCEMENT = null;
        }
    }

    public enum COLOUR{
        PURPLE(10),
        GRAY(7);

        public final String name;
        public final int dmg;

        COLOUR(int dmg){
            this.name = this.name();
            this.dmg = dmg;
        }

        public static COLOUR fromDamage(int dmg){
            COLOUR colour = null;
            for (COLOUR value : COLOUR.values()) {
                if(value.dmg==dmg){
                    colour = value;
                }
            }
            return colour;
        }
        public Material toColour(){
            return Material.getMaterial(name()+"_STAINED_GLASS_PANE");
        }
    }

}
