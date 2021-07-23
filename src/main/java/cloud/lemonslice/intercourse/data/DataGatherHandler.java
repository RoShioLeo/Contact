package cloud.lemonslice.intercourse.data;

import cloud.lemonslice.intercourse.data.provider.AdvancementProvider;
import cloud.lemonslice.intercourse.data.provider.RecipeProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

import static cloud.lemonslice.intercourse.Intercourse.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class DataGatherHandler
{
    @SubscribeEvent
    public static void onDataGather(GatherDataEvent event)
    {
        DataGenerator gen = event.getGenerator();
        if (event.includeServer())
        {
            gen.addProvider(new RecipeProvider(gen));
            gen.addProvider(new AdvancementProvider(gen));
        }
    }
}
