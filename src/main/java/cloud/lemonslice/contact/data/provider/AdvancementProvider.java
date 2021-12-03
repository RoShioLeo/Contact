package cloud.lemonslice.contact.data.provider;

import cloud.lemonslice.contact.Contact;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public final class AdvancementProvider implements DataProvider
{
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    private final DataGenerator generator;
    private final List<Consumer<Consumer<Advancement>>> advancements = ImmutableList.of(new AdvancementConsumer());

    public AdvancementProvider(DataGenerator generatorIn)
    {
        this.generator = generatorIn;
    }

    @Override
    public void run(HashCache cache)
    {
        Path path = this.generator.getOutputFolder();
        Set<ResourceLocation> set = Sets.newHashSet();
        Consumer<Advancement> consumer = (advancement) ->
        {
            if (!set.add(advancement.getId()))
            {
                throw new IllegalStateException("Duplicate advancement " + advancement.getId());
            }
            else
            {
                Path path1 = getPath(path, advancement);

                try
                {
                    DataProvider.save(GSON, cache, advancement.deconstruct().serializeToJson(), path1);
                }
                catch (IOException ioexception)
                {
                    Contact.error("Couldn't save advancement {}", path1, ioexception);
                }

            }
        };

        for (Consumer<Consumer<Advancement>> consumer1 : this.advancements)
        {
            consumer1.accept(consumer);
        }
    }

    private static Path getPath(Path pathIn, Advancement advancementIn)
    {
        return pathIn.resolve("data/" + advancementIn.getId().getNamespace() + "/advancements/" + advancementIn.getId().getPath() + ".json");
    }

    @Override
    public String getName()
    {
        return "Contact Advancements";
    }
}
