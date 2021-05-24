package me.tropicalshadow.arcanetable.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

public class ItemBuilder {

    private final Map<Enchantment,Integer> enchantments = new HashMap<>();
    private final ArrayList<String> lore = new ArrayList<>();
    private String playerHeadname = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDhiMmYzNmJmZGZhY2Q5NTdhNWY4YzQxY2NjZTM5ZWZlZjU0YzI1YWUxM2U0MDhiOGQ4YzFmYzQzMDhjYTcwIn19fQ==";
    private String language = Locale.ENGLISH.getLanguage();
    private boolean ignoreLevelRestriction = false;
    private int color = 0;
    private Material material = Material.AIR;
    private String name = "Unamed Item";
    private int count = 1;

    public ItemBuilder setName(String name){this.name = name;return this;}
    public ItemBuilder setLang(String lang){this.language = lang;return this;}
    public ItemBuilder setMaterial(Material mat){this.material = mat;return this;}
    public ItemBuilder setColor(int color){
        this.color = color;
        return this;
    }
    public ItemBuilder setCount(int count){this.count = count;return this;}
    public ItemBuilder addLore(String... str){
        if(str==null)return this;
        for (String s : str) {
            if(s!=null)this.lore.add(s);
        }
        return this;
    }
    public ItemBuilder addEnchantment(Enchantment ench , int level){this.enchantments.put(ench,level);return this;}
    public ItemBuilder setIgnoreLevelRestriction(boolean restriction){this.ignoreLevelRestriction = restriction;return this;}
    public ItemBuilder setPlayerHead(String headName){
        this.playerHeadname = headName;
        return this;
    }
    public ItemStack build(){
        ItemStack item;
        if(this.material == SkullUtils.createSkull().getType()){
            item = SkullUtils.itemFromBase64(this.playerHeadname);
        }if(VersionUtils.isLegacy && this.material.equals(Material.GLASS)) {
            Material mat = (Material.getMaterial("STAINED_GLASS_PANE") != null ? Material.getMaterial("STAINED_GLASS_PANE") : Material.GLASS);
            assert mat != null;
            item = new ItemStack(mat, this.count, (short) this.color);
        }else if(!VersionUtils.isLegacy && this.material.equals(Material.GLASS)){
            item = new ItemStack(VersionUtils.COLOUR.fromDamage(this.color).toColour());
        }else{
            item = new ItemStack(this.material);
        }
        item.setAmount(this.count);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',this.name));
        List<String> formattedLore = new ArrayList<>();
        if(!this.lore.isEmpty())
             formattedLore = this.lore.stream().map((str)-> ChatColor.translateAlternateColorCodes('&',str)).collect(Collectors.toList());
        meta.setLore(formattedLore);
        enchantments.forEach((ench,level)-> meta.addEnchant(ench,level,this.ignoreLevelRestriction));

        item.setItemMeta(updateEnchantmentVisuals(meta));
        return item;
    }

    public ItemMeta updateEnchantmentVisuals(ItemMeta meta){
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        ArrayList<String> enchants = new ArrayList<>();
        meta.getEnchants().forEach((ench,level)-> {
            if(!enchants.contains(ChatColor.stripColor(EnchantmentUtils.getEnchantDisplayWithRomanNum(ench,level,this.language))))
            enchants.add(EnchantmentUtils.getEnchantDisplayWithRomanNum(ench,level,this.language));
        });
        if(enchants.isEmpty())
            return meta;
        if(meta.getLore()==null || meta.getLore().isEmpty()){
            meta.setLore(enchants);
            return meta;
        }


        enchants.addAll(meta.getLore());

        meta.setLore(enchants);
        return meta;
    }



}
