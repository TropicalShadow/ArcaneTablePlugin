package me.tropicalshadow.arcanetable.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;


public class EnchantmentUtils {


    public static ArrayList<Enchantment> getAllEnchantments(){
        return (ArrayList<Enchantment>) Arrays.asList(Enchantment.values());
    }

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
    
    public static void purgeEnchantmentFromItem(ItemStack item, Enchantment ench){
        if(!item.containsEnchantment(ench))return;
        int level = item.getEnchantmentLevel(ench);
        assert item.getItemMeta() != null;
        List<Component> lore = Objects.requireNonNullElse(item.getItemMeta().lore(), new ArrayList<>());
        Collection<Component> loreToRemove = lore.stream().filter(line -> line == getEnchantDisplayWithRomanNum(ench,level)).collect(Collectors.toList());
        if(loreToRemove.size()>0){
            lore.removeAll(loreToRemove);
            item.getItemMeta().lore(lore);
        }
        item.removeEnchantment(ench);

    }


    public static TranslatableComponent getEnchantmentTranslateName(Enchantment ench){
        return Component.translatable("enchantment.minecraft."+ench.getKey().getKey());
    }
    
    public static String getEnchantmentName(Enchantment ench, @Nullable String language){
        EnchantmentCosts en = EnchantmentCosts.getFromEnchant(ench);
        String base;
        if(VersionUtils.isLegacy){
            if(language!=null){
                base = ench.getName().toLowerCase(new Locale(language));
            }else{
                base = ench.getName();
            }
        }else{
            if(language!=null){
                base = ench.getKey().getKey().toLowerCase(new Locale(language));
            }else{
                base = ench.getKey().getKey();
            }
        }

        String spacedName = base.replace('_',' ');
        return capitalizeFully(spacedName);
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
    public static Component getEnchantDisplayWithRomanNum(Enchantment ench, int level){
        Component romanNum = level > 10 ? Component.text(integerToRoman(level)) : Component.translatable("enchantment.level."+level);
        return getEnchantmentTranslateName(ench).append(Component.space().append(romanNum)).color(ench.isCursed() ? NamedTextColor.RED : NamedTextColor.GRAY );
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

    public static String capitalizeFully(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        StringBuilder result = new StringBuilder();
        String[] words = input.toLowerCase().split(" ");

        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
            }
        }

        return result.toString().trim();
    }
    private static final int[] values = { 1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1 };
    private static final String[] romanLiterals = { "M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I" };

    public static String integerToRoman(int number) {

        StringBuilder s = new StringBuilder();

        for (int i = 0; i < values.length; i++) {
            while (number >= values[i]) {
                number -= values[i];
                s.append(romanLiterals[i]);
            }
        }
        return s.toString();
    }

    public enum EnchantmentCosts{
        PROTECTION(0,4,1, VersionUtils.isLegacy ? Enchantment.getByName("protection") : Enchantment.getByKey(NamespacedKey.minecraft("protection"))),
        FIRE_PROTECTION(1,4,2,VersionUtils.isLegacy ? Enchantment.getByName("fire_protection") : Enchantment.getByKey(NamespacedKey.minecraft("fire_protection"))),
        FEATHER_FALLING(2,4,2,VersionUtils.isLegacy ? Enchantment.getByName("feather_falling") : Enchantment.getByKey(NamespacedKey.minecraft("feather_falling"))),
        BLAST_PROTECTION(3,4,4,VersionUtils.isLegacy ? Enchantment.getByName("blast_protection") : Enchantment.getByKey(NamespacedKey.minecraft("blast_protection"))),
        PROJECTILE_PROTECTION(4,4,2,VersionUtils.isLegacy ? Enchantment.getByName("projectile_protection") :Enchantment.getByKey(NamespacedKey.minecraft("projectile_protection"))),
        THORNS(5,3,8,VersionUtils.isLegacy ? Enchantment.getByName("thorns") :Enchantment.getByKey(NamespacedKey.minecraft("thorns"))),
        RESPIRATION(6,3,4,VersionUtils.isLegacy ? Enchantment.getByName("respiration") : Enchantment.getByKey(NamespacedKey.minecraft("respiration"))),
        DEPTH_STRIDER(7,3,4,VersionUtils.isLegacy ? Enchantment.getByName("depth_strider") :Enchantment.getByKey(NamespacedKey.minecraft("depth_strider"))),
        AQUA_AFFNITY(8,1,4,VersionUtils.isLegacy ? Enchantment.getByName("aqua_affinity") :Enchantment.getByKey(NamespacedKey.minecraft("aqua_affinity"))),
        SHARPNESS(9,5,1,VersionUtils.isLegacy ? Enchantment.getByName("sharpness") :Enchantment.getByKey(NamespacedKey.minecraft("sharpness"))),
        SMITE(10,5,2,VersionUtils.isLegacy ? Enchantment.getByName("smite") :Enchantment.getByKey(NamespacedKey.minecraft("smite"))),
        BANE_OF_ARTHROPODS(11,5,2,VersionUtils.isLegacy ? Enchantment.getByName("") :Enchantment.getByKey(NamespacedKey.minecraft("bane_of_arthropods"))),
        KNOCKBACK(12,2,2,VersionUtils.isLegacy ? Enchantment.getByName("bane_of_arthropods") :Enchantment.getByKey(NamespacedKey.minecraft("knockback"))),
        FIRE_ASPECT(13,2,4,VersionUtils.isLegacy ? Enchantment.getByName("fire_aspect") :Enchantment.getByKey(NamespacedKey.minecraft("fire_aspect"))),
        LOOTING(14,3,4,VersionUtils.isLegacy ? Enchantment.getByName("looting") :Enchantment.getByKey(NamespacedKey.minecraft("looting"))),
        EFFICIENCY(15,5,1,VersionUtils.isLegacy ? Enchantment.getByName("efficiency") :Enchantment.getByKey(NamespacedKey.minecraft("efficiency"))),
        SILK_TOUCH(16,1,8,VersionUtils.isLegacy ? Enchantment.getByName("silk_touch") :Enchantment.getByKey(NamespacedKey.minecraft("silk_touch"))),
        UNBREAKING(17,3,2,VersionUtils.isLegacy ? Enchantment.getByName("unbreaking") :Enchantment.getByKey(NamespacedKey.minecraft("unbreaking"))),
        FORTUNE(18,3,4,VersionUtils.isLegacy ? Enchantment.getByName("fortune") :Enchantment.getByKey(NamespacedKey.minecraft("fortune"))),
        POWER(19,5,1,VersionUtils.isLegacy ? Enchantment.getByName("fortune") :Enchantment.getByKey(NamespacedKey.minecraft("power"))),
        PUNCH(20,2,4,VersionUtils.isLegacy ? Enchantment.getByName("punch") :Enchantment.getByKey(NamespacedKey.minecraft("punch"))),
        FLAME(21,1,4,VersionUtils.isLegacy ? Enchantment.getByName("flame") :Enchantment.getByKey(NamespacedKey.minecraft("flame"))),
        INFINITY(22,1,8,VersionUtils.isLegacy ? Enchantment.getByName("infinity") :Enchantment.getByKey(NamespacedKey.minecraft("infinity"))),
        LUCK_OF_THE_SEA(23,3,4,VersionUtils.isLegacy ? Enchantment.getByName("luck_of_the_sea") :Enchantment.getByKey(NamespacedKey.minecraft("luck_of_the_sea"))),
        LURE(24,3,4,VersionUtils.isLegacy ? Enchantment.getByName("lure") :Enchantment.getByKey(NamespacedKey.minecraft("lure"))),
        FROST_WALKER(25,2,4,VersionUtils.isLegacy ? Enchantment.getByName("frost_walker") :Enchantment.getByKey(NamespacedKey.minecraft("frost_walker"))),
        MENDING(26,1,4,VersionUtils.isLegacy ? Enchantment.getByName("mending") :Enchantment.getByKey(NamespacedKey.minecraft("mending"))),
        CURSE_OF_BINDING(27,1,8,VersionUtils.isLegacy ? Enchantment.getByName("binding_curse") :Enchantment.getByKey(NamespacedKey.minecraft("binding_curse"))),
        CURSE_OF_VANISHING(28,1,8,VersionUtils.isLegacy ? Enchantment.getByName("vanishing_curse") :Enchantment.getByKey(NamespacedKey.minecraft("vanishing_curse"))),
        IMPALING(29,5,4,VersionUtils.isLegacy ? Enchantment.getByName("impaling") :Enchantment.getByKey(NamespacedKey.minecraft("impaling"))),
        RIPTIDE(30,3,4,VersionUtils.isLegacy ? Enchantment.getByName("riptide") :Enchantment.getByKey(NamespacedKey.minecraft("riptide"))),
        LOYALTY(31,3,1,VersionUtils.isLegacy ? Enchantment.getByName("loyalty") :Enchantment.getByKey(NamespacedKey.minecraft("loyalty"))),
        CHANNELING(32,1,8,VersionUtils.isLegacy ? Enchantment.getByName("channeling") :Enchantment.getByKey(NamespacedKey.minecraft("channeling"))),
        MULTISHOT(33,1,4,VersionUtils.isLegacy ? Enchantment.getByName("multishot") :Enchantment.getByKey(NamespacedKey.minecraft("multishot"))),
        PIERCING(34,4,1,VersionUtils.isLegacy ? Enchantment.getByName("piercing") :Enchantment.getByKey(NamespacedKey.minecraft("piercing"))),
        QUICK_CHARGE(35,3,2,VersionUtils.isLegacy ? Enchantment.getByName("quick_charge") :Enchantment.getByKey(NamespacedKey.minecraft("quick_charge"))),
        SOUL_SPEED(36,3,8,VersionUtils.isLegacy ? Enchantment.getByName("soul_speed") :Enchantment.getByKey(NamespacedKey.minecraft("soul_speed"))),
        SWEEPING_EDGE(37,3,4,VersionUtils.isLegacy ? Enchantment.getByName("sweeping") :Enchantment.getByKey(NamespacedKey.minecraft("sweeping"))),
        SWIFT_SNEEK(38,5,4, VersionUtils.versionID < 19 ? null : Enchantment.getByKey(NamespacedKey.minecraft("swift_sneek"))),
        UNKNOWN(6699,0,1,null)
        ;

        public final int id;
        public final int maxLevel;
        public final int itemMulti;
        public final Enchantment ench;
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
