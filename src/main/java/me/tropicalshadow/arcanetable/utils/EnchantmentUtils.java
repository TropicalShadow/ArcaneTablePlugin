package me.tropicalshadow.arcanetable.utils;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class EnchantmentUtils {

    public static ArrayList<Enchantment> getCanEnchants(ItemStack item){
        ArrayList<Enchantment> output = new ArrayList<>();
        for (Enchantment ench : Enchantment.values()) {
            if(ench.canEnchantItem(new ItemStack(item.getType()))){
                if(ench.equals(Enchantment.BINDING_CURSE) || ench.equals(Enchantment.VANISHING_CURSE))
                    continue;
                output.add(ench);
            }
        }
        return output;
    }
    public static String getEnchantmentName(Enchantment ench){
        String base = ench.getKey().getKey();
        String spacedName = base.replace('_',' ');
        return WordUtils.capitalizeFully(spacedName);
    }
    public static ItemStack applyEnchantToItem(ItemStack item, Enchantment ench, int level,boolean isUnsafe, boolean removeConflitcs){
        if(removeConflitcs){
            for (Enchantment conflictingEnchantment : findConflictingEnchantments(item, ench)) {
                if(conflictingEnchantment!=null)
                    item.removeEnchantment(conflictingEnchantment);
            }
            if(item.containsEnchantment(ench))
                item.removeEnchantment(ench);

        }
        if(isUnsafe){
            item.addEnchantment(ench,level);
        }else{
            item.addUnsafeEnchantment(ench,level);
        }
        return item;
    }

    public static ArrayList<Enchantment> findConflictingEnchantments(ItemStack item, Enchantment counter){
        Map<Enchantment, Integer> map = item.getEnchantments();
        ArrayList<Enchantment> conflicts = new ArrayList<>();
        if(map.isEmpty())return conflicts;
        map.keySet().forEach(ench->{
            if(ench.conflictsWith(counter) && !ench.equals(counter)){
                conflicts.add(ench);
            }
        });

        return conflicts;
    }
    public static boolean doesItemAlreadyHasEnchant(ItemStack item, Enchantment ench, int level){
        return (item.containsEnchantment(ench) && item.getEnchantmentLevel(ench)==level && level != 0);
    }
    public static boolean itemHasHigherEnchantmentLevel(ItemStack item, Enchantment ench, int level){
        return (item.containsEnchantment(ench) && item.getEnchantmentLevel(ench)>level && level != 0);
    }
    public static int getEnchantmentCost(Enchantment ench,int level){
        return EnchantmentCosts.getFromEnchant(ench).getCost(level);
    }
    public enum EnchantmentCosts{
        PROTECTION(0,4,1, Enchantment.PROTECTION_ENVIRONMENTAL),
        FIRE_PROTECTION(1,4,2,Enchantment.PROTECTION_FIRE),
        FEATHER_FALLING(2,4,2,Enchantment.PROTECTION_FALL),
        BLAST_PROTECTION(3,4,4,Enchantment.PROTECTION_EXPLOSIONS),
        PROJECTILE_PROTECTION(4,4,2,Enchantment.PROTECTION_PROJECTILE),
        THORNS(5,3,8,Enchantment.THORNS),
        RESPIRATION(6,3,4,Enchantment.OXYGEN),
        DEPTH_STRIDER(7,3,4,Enchantment.DEPTH_STRIDER),
        AQUA_AFFNITY(8,1,4,Enchantment.WATER_WORKER),
        SHARPNESS(9,5,1,Enchantment.DAMAGE_ALL),
        SMITE(10,5,2,Enchantment.DAMAGE_UNDEAD),
        BANE_OF_ARTHROPODS(11,5,2,Enchantment.DAMAGE_ARTHROPODS),
        KNOCKBACK(12,2,2,Enchantment.KNOCKBACK),
        FIRE_ASPECT(13,2,4,Enchantment.FIRE_ASPECT),
        LOOTING(14,3,4,Enchantment.LOOT_BONUS_MOBS),
        EFFICIENCY(15,5,1,Enchantment.DIG_SPEED),
        SILK_TOUCH(16,1,8,Enchantment.SILK_TOUCH),
        UNBREAKING(17,3,2,Enchantment.DURABILITY),
        FORTUNE(18,3,4,Enchantment.LOOT_BONUS_BLOCKS),
        POWER(19,5,1,Enchantment.ARROW_DAMAGE),
        PUNCH(20,2,4,Enchantment.ARROW_KNOCKBACK),
        FLAME(21,1,4,Enchantment.ARROW_FIRE),
        INFINITY(22,1,8,Enchantment.ARROW_INFINITE),
        LUCK_OF_THE_SEA(23,3,4,Enchantment.LUCK),
        LURE(24,3,4,Enchantment.LURE),
        FROST_WALKER(25,2,4,Enchantment.FROST_WALKER),
        MENDING(26,1,4,Enchantment.MENDING),
        CURSE_OF_BINDING(27,1,8,Enchantment.BINDING_CURSE),
        CURSE_OF_VANISHING(28,1,8,Enchantment.VANISHING_CURSE),
        IMPALING(29,5,4,Enchantment.IMPALING),
        RIPTIDE(30,3,4,Enchantment.RIPTIDE),
        LOYALTY(31,3,1,Enchantment.LOYALTY),
        CHANNELING(32,1,8,Enchantment.CHANNELING),
        MULTISHOT(33,1,4,Enchantment.MULTISHOT),
        PIERCING(34,4,1,Enchantment.PIERCING),
        QUICK_CHARGE(35,3,2,Enchantment.QUICK_CHARGE),
        SOUL_SPEED(36,3,8,Enchantment.SOUL_SPEED),
        SWEEPING_EDGE(37,3,4,Enchantment.SWEEPING_EDGE),
        UNKNOWN(6699,0,1,null)
        ;

        public int id;
        public int maxLevel;
        public int itemMulti;
        public Enchantment ench;
        EnchantmentCosts(int id, int maxLevel, int itemMulti,Enchantment ench) {
            this.id = id;
            this.maxLevel = maxLevel;
            this.itemMulti = itemMulti;
            this.ench = ench;
        }

        public Enchantment getEnch() {
            return ench;
        }

        public int getCost(int level){
            return (level*this.itemMulti)+15;
        }

        public static EnchantmentCosts getFromEnchant(Enchantment ench){
            EnchantmentCosts selected = null;
            for (EnchantmentCosts enchant : EnchantmentCosts.values()) {
                if(enchant.getEnch()==null)continue;
                if(enchant.getEnch().equals(ench)){
                    selected = enchant;
                }
            }
            return (selected == null) ? UNKNOWN : selected;
        }
    }
}
