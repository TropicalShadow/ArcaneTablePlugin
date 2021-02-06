package me.tropicalshadow.arcanetable.utils;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class EnchantmentUtils {

    public static ArrayList<Enchantment> getCanEnchants(ItemStack item){
        ArrayList<Enchantment> output = new ArrayList<>();
        for (Enchantment ench : Enchantment.values()) {
            if(ench.canEnchantItem(new ItemStack(item.getType())) && ench != Enchantment.BINDING_CURSE && ench != Enchantment.VANISHING_CURSE ){
                output.add(ench);
            }
        }
        return output;
    }
}
