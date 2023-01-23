package cloud.lemonslice.contact.common.item;

import cloud.lemonslice.contact.Contact;
import cloud.lemonslice.contact.common.screenhandler.EnvelopeScreenHandler;
import cloud.lemonslice.silveroak.common.item.NormalItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import static cloud.lemonslice.contact.Contact.MODID;

public class EnvelopeItem extends NormalItem
{
    private static final Text CONTAINER_NAME = Text.translatable("container.contact.envelope");

    public EnvelopeItem()
    {
        super(new Identifier(MODID, "envelope"), Contact.ITEM_GROUP);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
    {
        ItemStack itemStack = user.getStackInHand(hand);
        if (!world.isClient())
        {
            user.openHandledScreen(getContainer());
            if (!user.getAbilities().creativeMode)
            {
                itemStack.decrement(1);
            }
        }
        return TypedActionResult.consume(itemStack);
    }

    public static NamedScreenHandlerFactory getContainer()
    {
        return new SimpleNamedScreenHandlerFactory((id, inventory, player) -> new EnvelopeScreenHandler(id, inventory), CONTAINER_NAME);
    }
}
