package cloud.lemonslice.contact.common.handler;

import cloud.lemonslice.contact.common.item.PostcardItem;
import cloud.lemonslice.contact.resourse.PostcardHandler;
import cloud.lemonslice.contact.resourse.PostcardStyle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.village.TradeOffer;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public final class WanderingTraderSaleHandler
{
    public static ActionResult onPlayerRightClickEntity(PlayerEntity player, World world, Hand hand, Entity entity, @Nullable EntityHitResult hitResult)
    {
        if (!world.isClient())
        {
            if (entity instanceof WanderingTraderEntity trader)
            {
                if (!trader.getCommandTags().contains("SellPostcard"))
                {
                    int i = world.getRandom().nextInt(PostcardHandler.POSTCARD_MANAGER.getPostcards().size());
                    trader.addCommandTag("SellPostcard");
                    Identifier id = PostcardHandler.POSTCARD_MANAGER.getPostcards().keySet().toArray(new Identifier[0])[i];
                    PostcardStyle style = PostcardHandler.POSTCARD_MANAGER.getPostcard(id);
                    while (!style.soldByTrader)
                    {
                        i = world.getRandom().nextInt(PostcardHandler.POSTCARD_MANAGER.getPostcards().size());
                        id = PostcardHandler.POSTCARD_MANAGER.getPostcards().keySet().toArray(new Identifier[0])[i];
                        style = PostcardHandler.POSTCARD_MANAGER.getPostcard(id);
                    }
                    trader.getOffers().add(0, new TradeOffer(style.cardPrice, new ItemStack(Items.ENDER_PEARL), PostcardItem.getPostcard(id, true), 16, 10, 0.05F));
                    trader.getOffers().add(0, new TradeOffer(style.cardPrice, PostcardItem.getPostcard(id, false), 16, 10, 0.05F));
                }
            }
        }
        return ActionResult.PASS;
    }
}
