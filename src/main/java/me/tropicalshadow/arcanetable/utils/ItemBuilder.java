package me.tropicalshadow.arcanetable.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

public class ItemBuilder {

    private Map<Enchantment,Integer> enchantments = new HashMap<>();
    private ArrayList<String> lore = new ArrayList<>();
    private Material material = Material.AIR;
    private boolean ignoreLevelRestriction = false;
    private String name = "Unamed Item";
    private int count = 1;

    public ItemBuilder setName(String name){this.name = name;return this;}
    public ItemBuilder setMaterial(Material mat){this.material = mat;return this;}
    public ItemBuilder setCount(int count){this.count = count;return this;}
    public ItemBuilder addLore(String... str){this.lore.addAll(Arrays.asList(str));return this;}
    public ItemBuilder addEnchantment(Enchantment ench , int level){this.enchantments.put(ench,level);return this;}
    public ItemBuilder setIgnoreLevelRestriction(boolean restriction){this.ignoreLevelRestriction = restriction;return this;}

    public ItemStack build(){
        ItemStack item = new ItemStack(this.material);
        item.setAmount(this.count);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',this.name));
        List<String> formattedLore = this.lore.stream().map((str)-> ChatColor.translateAlternateColorCodes('&',str)).collect(Collectors.toList());
        meta.setLore(formattedLore);
        enchantments.forEach((ench,level)->{
            meta.addEnchant(ench,level,this.ignoreLevelRestriction);
        });

        item.setItemMeta(meta);
        return item;
    }


}
