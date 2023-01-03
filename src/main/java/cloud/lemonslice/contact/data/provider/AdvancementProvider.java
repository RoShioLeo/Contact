package cloud.lemonslice.contact.data.provider;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;

import java.util.concurrent.CompletableFuture;

public final class AdvancementProvider extends ForgeAdvancementProvider
{
    public AdvancementProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper existingFileHelper)
    {
        super(output, registries, existingFileHelper, ImmutableList.of(new AdvancementConsumer()));
    }
}
