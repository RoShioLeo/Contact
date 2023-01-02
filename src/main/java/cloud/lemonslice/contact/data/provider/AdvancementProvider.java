package cloud.lemonslice.contact.data.provider;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;

import java.util.function.Consumer;

public final class AdvancementProvider extends FabricAdvancementProvider
{
    private final Consumer<Consumer<Advancement>> advancements = new AdvancementConsumer();

    public AdvancementProvider(FabricDataOutput dataOutput)
    {
        super(dataOutput);
    }

    @Override
    public void generateAdvancement(Consumer<Advancement> consumer)
    {
        advancements.accept(consumer);
    }

    @Override
    public String getName()
    {
        return "Contact Advancements";
    }
}
