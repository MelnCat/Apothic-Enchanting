package dev.shadowsoffire.apothic_enchanting.payloads;

import java.util.List;
import java.util.Optional;

import dev.shadowsoffire.apothic_enchanting.ApothicEnchanting;
import dev.shadowsoffire.apothic_enchanting.table.ApothEnchantmentScreen;
import dev.shadowsoffire.apothic_enchanting.table.EnchantmentTableStats;
import dev.shadowsoffire.placebo.network.PayloadHelper;
import dev.shadowsoffire.placebo.network.PayloadProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class StatsPayload implements CustomPacketPayload {

    public static final ResourceLocation ID = ApothicEnchanting.loc("stats");

    protected final EnchantmentTableStats stats;

    public StatsPayload(EnchantmentTableStats stats) {
        this.stats = stats;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        this.stats.write(buf);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public static class Provider implements PayloadProvider<StatsPayload, PlayPayloadContext> {

        @Override
        public ResourceLocation id() {
            return ID;
        }

        @Override
        public StatsPayload read(FriendlyByteBuf buf) {
            return new StatsPayload(EnchantmentTableStats.read(buf));
        }

        @Override
        public void handle(StatsPayload msg, PlayPayloadContext ctx) {
            PayloadHelper.handle(() -> {
                if (Minecraft.getInstance().screen instanceof ApothEnchantmentScreen es) {
                    es.getMenu().setStats(msg.stats);
                }
            }, ctx);
        }

        @Override
        public List<ConnectionProtocol> getSupportedProtocols() {
            return List.of(ConnectionProtocol.PLAY);
        }

        @Override
        public Optional<PacketFlow> getFlow() {
            return Optional.of(PacketFlow.CLIENTBOUND);
        }

    }

}
