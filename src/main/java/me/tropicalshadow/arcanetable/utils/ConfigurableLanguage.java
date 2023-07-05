package me.tropicalshadow.arcanetable.utils;

import me.tropicalshadow.arcanetable.ArcaneTable;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;

public enum ConfigurableLanguage {
    PLACE_ITEM_NAME("Place item above", "place-item-above.name"),
    PLACE_ITEM_LORE("Place item above", "place-item-above.lore"),

    GO_BACK_NAME("&cGo Back", "go-back.name"),
    GO_BACK_LORE("&7Click to go back", "go-back.lore"),

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

    public List<String> getTextList(){
        YamlConfiguration config = ArcaneTable.getPlugin().langConfig;
        return config.isList(getConfigLocation()) ? config.getStringList(getConfigLocation()) : List.of(getDefaultText());
    }

}
