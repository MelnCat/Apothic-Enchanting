package dev.shadowsoffire.apothic_enchanting.objects;

import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import dev.shadowsoffire.apothic_enchanting.util.TooltipUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.neoforge.event.AnvilUpdateEvent;

public class ScrappingTomeItem extends BookItem {

    static Random rand = new Random();

    public ScrappingTomeItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag) {
        if (stack.isEnchanted()) return;
        tooltip.add(TooltipUtil.lang("info", "scrap_tome").withStyle(ChatFormatting.GRAY));
        tooltip.add(TooltipUtil.lang("info", "scrap_tome2").withStyle(ChatFormatting.GRAY));
    }

    public static boolean updateAnvil(AnvilUpdateEvent ev) {
        ItemStack weapon = ev.getLeft();
        ItemStack book = ev.getRight();
        if (!(book.getItem() instanceof ScrappingTomeItem) || book.isEnchanted() || !weapon.isEnchanted()) return false;

        ItemEnchantments.Mutable wepEnch = new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(weapon));
        int size = Mth.ceil(wepEnch.keySet().size() / 2D);
        List<Holder<Enchantment>> keys = Lists.newArrayList(wepEnch.keySet());
        long seed = 1831;
        for (Holder<Enchantment> e : keys) {
            seed ^= e.getKey().hashCode();
        }
        seed ^= ev.getPlayer().getEnchantmentSeed();
        rand.setSeed(seed);
        while (wepEnch.keySet().size() > size) {
            Holder<Enchantment> lost = keys.get(rand.nextInt(keys.size()));
            wepEnch.keySet().remove(lost);
            keys.remove(lost);
        }
        ItemStack out = new ItemStack(Items.ENCHANTED_BOOK);
        EnchantmentHelper.setEnchantments(out, wepEnch.toImmutable());
        ev.setMaterialCost(1);
        ev.setCost(wepEnch.keySet().size() * 6);
        ev.setOutput(out);
        return true;
    }
}
