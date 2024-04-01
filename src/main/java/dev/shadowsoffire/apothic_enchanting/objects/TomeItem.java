package dev.shadowsoffire.apothic_enchanting.objects;

import java.util.List;

import dev.shadowsoffire.apothic_enchanting.ApothicEnchanting;
import dev.shadowsoffire.apothic_enchanting.api.EnchantableItem;
import dev.shadowsoffire.apothic_enchanting.util.TooltipUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BookItem;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class TomeItem extends BookItem implements EnchantableItem {

    final ItemStack rep;
    final EnchantmentCategory type;

    public TomeItem(Item rep, EnchantmentCategory type) {
        super(new Item.Properties());
        this.type = type;
        this.rep = new ItemStack(rep);
        ApothicEnchanting.TYPED_BOOKS.add(this);
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return stack.getCount() == 1;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        if (this.type == null) return ApothicEnchanting.TYPED_BOOKS.stream().filter(b -> b != this).allMatch(b -> !enchantment.canEnchant(new ItemStack(b)));
        return enchantment.canApplyAtEnchantingTable(this.rep);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(TooltipUtil.lang("info", BuiltInRegistries.ITEM.getKey(this).getPath()).withStyle(ChatFormatting.GRAY));
        if (stack.isEnchanted()) {
            tooltip.add(TooltipUtil.lang("info", "tome_error").withStyle(ChatFormatting.RED));
        }
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return !stack.isEnchanted() ? super.getRarity(stack) : Rarity.UNCOMMON;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.isEnchanted()) {
            ItemStack book = new ItemStack(Items.ENCHANTED_BOOK, stack.getCount());
            EnchantmentHelper.setEnchantments(EnchantmentHelper.getEnchantments(stack), book);
            return InteractionResultHolder.consume(book);
        }
        return InteractionResultHolder.pass(stack);
    }

    @Override
    public ItemStack applyEnchantments(ItemStack stack, List<EnchantmentInstance> enchantments) {
        stack = new ItemStack(Items.ENCHANTED_BOOK);
        for (EnchantmentInstance inst : enchantments) {
            EnchantedBookItem.addEnchantment(stack, inst);
        }
        return stack;
    }

    @Override
    public boolean forciblyAllowsTableEnchantment(ItemStack stack, Enchantment enchantment) {
        return this.canApplyAtEnchantingTable(stack, enchantment);
    }

}
