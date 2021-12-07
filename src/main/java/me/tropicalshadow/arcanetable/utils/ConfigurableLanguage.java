package me.tropicalshadow.arcanetable.utils;

import me.tropicalshadow.arcanetable.ArcaneTable;
import org.bukkit.configuration.file.YamlConfiguration;

public enum ConfigurableLanguage {
    PLACE_ITEM_IN_SLOT("Place item in slot above", "place-item-in-slot"),
    PLACE_ITEM_ABOVE("Place item above","place-item-above"),
    ALREADY_HAS_LINE_1("&4Item already has this enchantment", "already-has.line-1"),
    ALREADY_HAS_LINE_2("&4Clicking will remove this enchant", "already-has.line-2"),
    ENCHANTMENT_HAS_CONFLICT("&4Enchantment conflicts with current enchants","enchantment-has-conflict"),
    CONFLICT_LINE_1("&5Enchantment conflicts with current enchants", "conflict.line-1"),
    CONFLICT_LINE_2("&5Clicking will remove conflicting enchants", "conflict.line-2"),
    HAS_HIGHER_LINE_1("&bItem already has higher level", "has-higher.line-1"),
    HAS_HIGHER_LINE_2("&bof this enchantment","has-higher.line-2"),
    PAGE_CURRENT("Page %1$s / %2%s", "page.current"),
    PAGE_NEXT("Next Page", "page.next"),
    PAGE_PREV("Prev Page", "page.prev")
    ;


    private final String defaultText;
    private final String configLocation;

    ConfigurableLanguage(String defaultText, String configLocation){
        this.defaultText = defaultText;
        this.configLocation = configLocation;
    }

    public String getConfigLocation() {
        return configLocation;
    }

    public String getDefaultText() {
        return defaultText;
    }
    public String getText(){
        YamlConfiguration config = ArcaneTable.getPlugin().langConfig;
        return config.isString(getConfigLocation()) ? config.getString(getConfigLocation()) : getDefaultText();
    }
}
