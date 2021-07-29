package cloud.lemonslice.contact.common.item;

import cloud.lemonslice.contact.Contact;
import cloud.lemonslice.contact.common.container.WrappingPaperContainer;
import cloud.lemonslice.silveroak.common.item.NormalItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class WrappingPaperItem extends NormalItem
{
    private static final ITextComponent CONTAINER_NAME = new TranslationTextComponent("container.contact.wrapping_paper");

    public WrappingPaperItem(String name)
    {
        super(name, NormalItem.getNormalItemProperties(Contact.ITEM_GROUP));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn)
    {
        ItemStack itemStack = playerIn.getHeldItem(handIn);
        if (!worldIn.isRemote)
        {
            NetworkHooks.openGui((ServerPlayerEntity) playerIn, getContainer(itemStack.getItem() == ItemRegistry.ENDER_WRAPPING_PAPER));
            if (!playerIn.abilities.isCreativeMode)
            {
                itemStack.shrink(1);
            }
        }
        return ActionResult.resultConsume(itemStack);
    }

    public static INamedContainerProvider getContainer(boolean isEnder)
    {
        return new SimpleNamedContainerProvider((id, inventory, player) -> new WrappingPaperContainer(id, inventory, isEnder), CONTAINER_NAME);
    }
}
