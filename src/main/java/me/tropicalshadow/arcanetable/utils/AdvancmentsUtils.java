package me.tropicalshadow.arcanetable.utils;

import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;

public class AdvancmentsUtils {

    private final Advancement advancment;

    public AdvancmentsUtils(Advancement advancement){
        this.advancment = advancement;
    }

    public boolean grant(Player player){
        AdvancementProgress progress = player.getAdvancementProgress(advancment);
        if(progress.isDone())
            return false;
        progress.getRemainingCriteria().forEach(progress::awardCriteria);

        return progress.isDone();
    }
}

