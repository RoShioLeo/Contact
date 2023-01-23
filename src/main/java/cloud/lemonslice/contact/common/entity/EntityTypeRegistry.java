package cloud.lemonslice.contact.common.entity;

import cloud.lemonslice.contact.Contact;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class EntityTypeRegistry
{
    public static final EntityType<PostcardEntity> POSTCARD;

    public static void init()
    {

    }

    private static <T extends Entity> EntityType<T> register(Identifier id, EntityType.Builder<T> type)
    {
        return Registry.register(Registries.ENTITY_TYPE, id, type.build(id.toString()));
    }

    static
    {
        EntityType.Builder<PostcardEntity> builder = EntityType.Builder.create((PostcardEntity::new), SpawnGroup.MISC);
        builder.setDimensions(0.5f, 0.5f).maxTrackingRange(10).trackingTickInterval(Integer.MAX_VALUE);
        POSTCARD = register(Contact.getIdentifier("postcard"), builder);
    }
}
