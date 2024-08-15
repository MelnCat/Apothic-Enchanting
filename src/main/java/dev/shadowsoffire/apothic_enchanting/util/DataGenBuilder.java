package dev.shadowsoffire.apothic_enchanting.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.RegistrySetBuilder.RegistryBootstrap;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

/**
 * Builder to help adding multiple {@link RegistryBootstrap}s and {@link DataProvider}s to the {@link GatherDataEvent}.
 * <p>
 * Handles creation of the {@link DatapackBuiltinEntriesProvider} and passing the correct {@link HolderLookup.Provider} to the data providers.
 */
public class DataGenBuilder {

    protected final Set<String> registrySetModids;
    protected final RegistrySetBuilder registrySet = new RegistrySetBuilder();
    protected final List<DataProviderFactory<?>> providers = new ArrayList<>();

    public static DataGenBuilder create(String... modids) {
        return new DataGenBuilder(modids);
    }

    protected DataGenBuilder(String... modids) {
        this.registrySetModids = Set.of(modids);
    }

    public <R> DataGenBuilder registry(ResourceKey<? extends Registry<R>> key, RegistrySetBuilder.RegistryBootstrap<R> bootstrap) {
        this.registrySet.add(key, bootstrap);
        return this;
    }

    public <T extends DataProvider> DataGenBuilder provider(DataProviderFactory<T> factory) {
        this.providers.add(factory);
        return this;
    }

    public <T extends DataProvider> DataGenBuilder provider(BiFunction<PackOutput, CompletableFuture<HolderLookup.Provider>, T> factory) {
        return this.provider((output, registries, fileHelper) -> factory.apply(output, registries));
    }

    public <T extends DataProvider> DataGenBuilder provider(Function<PackOutput, T> factory) {
        return this.provider((output, registries, fileHelper) -> factory.apply(output));
    }

    public void build(GatherDataEvent event) {
        this.registerDataProviders(event);
    }

    protected void registerDataProviders(GatherDataEvent event) {
        PackOutput output = event.getGenerator().getPackOutput();

        DatapackBuiltinEntriesProvider datapackProvider = new DatapackBuiltinEntriesProvider(output, event.getLookupProvider(), this.registrySet, this.registrySetModids);
        CompletableFuture<HolderLookup.Provider> registries = datapackProvider.getRegistryProvider();

        DataGenerator generator = event.getGenerator();
        generator.addProvider(true, datapackProvider);
        for (DataProviderFactory<?> factory : this.providers) {
            generator.addProvider(true, factory.create(output, registries, event.getExistingFileHelper()));
        }
    }

    @FunctionalInterface
    public static interface DataProviderFactory<T extends DataProvider> {
        T create(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper fileHelper);
    }

}
