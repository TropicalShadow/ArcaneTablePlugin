package me.tropicalshadow.arcanetable.utils;

import me.tropicalshadow.arcanetable.ArcaneTable;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.UnknownNullability;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VersionUtils {

    public static String bukkitVersion = Bukkit.getVersion();

    @UnknownNullability
    private static Version serverVersion = null;

    public static boolean isLegacy = false;

    public static void versionControl(){
        Logging.info(bukkitVersion);
        Version version = VersionUtils.getServerVersion();
        if(version == null){
            isLegacy = true;
        }else {
            if(version.getMinor() < 13){
                isLegacy = true;
            }
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

    @UnknownNullability
    public static Version getServerVersion(){
        Version version = serverVersion;
        if(version == null){
            version = updateServerVersion();
        }
        return version;
    }

    public static Version updateServerVersion(){
        Matcher m = Pattern.compile("\\d+\\.\\d+(\\.\\d+)?").matcher(bukkitVersion);
        if (!m.find()) {
            serverVersion = new Version(666, 0, 0);
        } else {
            serverVersion = new Version(m.group());
        }
        return serverVersion;
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
