package me.tropicalshadow.arcanetable.utils;

import me.tropicalshadow.arcanetable.ArcaneTable;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.List;

public class PluginExtensionsUtils {

    private static PluginExtensionsUtils INSTANCE = null;
    public static Class<?> AEAPI = null;


    public PluginExtensionsUtils(){
        Plugin AdvEnch = ArcaneTable.getPlugin().getServer().getPluginManager().getPlugin("AdvancedEnchantments");
        INSTANCE = this;
        if(AdvEnch!=null){
            Logging.info(ChatColor.GOLD+"Advanced Enchantments has been "+ ChatColor.GREEN +"hooked");
            try {
                AEAPI = Class.forName("n3kas.ae.api.AEAPI");
                Method getAllEnchantments = AEAPI.getMethod("getAllEnchantments");
                List<String> output = (List<String>) getAllEnchantments.invoke(this);

            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    public static List<String> getExtraEnchantments(){
        try{
            AEAPI = Class.forName("n3kas.ae.api.AEAPI");
            Method getAllEnchantments = AEAPI.getMethod("getAllEnchantments");

            return (List<String>) getAllEnchantments.invoke(INSTANCE);
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
