package cloud.lemonslice.contact.common.item;

import cloud.lemonslice.contact.Contact;
import cloud.lemonslice.contact.common.container.WrappingPaperContainer;
import cloud.lemonslice.silveroak.common.item.NormalItem;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

public class WrappingPaperItem extends NormalItem
{
    private static final Component CONTAINER_NAME = new TranslatableComponent("container.contact.wrapping_paper");

    public WrappingPaperItem()
    {
        super(Contact.ITEM_GROUP);
    }

    public static MenuProvider getContainer(boolean isEnder)
    {
        return new SimpleMenuProvider((id, inventory, player) -> new WrappingPaperContainer(id, inventory, isEnder), CONTAINER_NAME);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn)
    {
        ItemStack itemStack = playerIn.getItemInHand(handIn);
        if (!worldIn.isClientSide)
        {
            NetworkHooks.openGui((ServerPlayer) playerIn, getContainer(itemStack.getItem() == ItemRegistry.ENDER_WRAPPING_PAPER.get()));
            if (!playerIn.getAbilities().instabuild)
            {
                itemStack.shrink(1);
            }
        }
        return InteractionResultHolder.consume(itemStack);
    }
}
