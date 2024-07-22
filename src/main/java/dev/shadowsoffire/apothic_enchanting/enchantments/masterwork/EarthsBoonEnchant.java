package dev.shadowsoffire.apothic_enchanting.enchantments.masterwork;

import dev.shadowsoffire.apothic_enchanting.ApothicEnchanting;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.enchantment.Enchantment;

public class EarthsBoonEnchant extends Enchantment {

    public EarthsBoonEnchant() {
        super(Rarity.VERY_RARE, ApothicEnchanting.PICKAXE, new EquipmentSlot[] { EquipmentSlot.MAINHAND });
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public int getMinCost(int level) {
        return 60 + (level - 1) * 20;
    }

    @Override
    public int getMaxCost(int enchantmentLevel) {
        return 200;
    }

    @Override
    public Component getFullname(int level) {
        return ((MutableComponent) super.getFullname(level)).withStyle(ChatFormatting.DARK_GREEN);
    }

}
