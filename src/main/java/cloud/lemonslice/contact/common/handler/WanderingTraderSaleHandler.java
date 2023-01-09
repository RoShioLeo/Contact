package cloud.lemonslice.contact.common.handler;

import cloud.lemonslice.contact.common.item.PostcardItem;
import cloud.lemonslice.contact.resourse.PostcardHandler;
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
                if (!trader.getScoreboardTags().contains("SellPostcard"))
                {
                    int i = world.getRandom().nextInt(PostcardHandler.POSTCARD_MANAGER.getPostcards().size());
                    trader.getScoreboardTags().add("SellPostcard");
                    Identifier[] list = PostcardHandler.POSTCARD_MANAGER.getPostcards().keySet().toArray(new Identifier[0]);
                    trader.getOffers().add(0, new TradeOffer(new ItemStack(Items.EMERALD), new ItemStack(Items.ENDER_PEARL), PostcardItem.getPostcard(list[i], true), 16, 10, 0.05F));
                    trader.getOffers().add(0, new TradeOffer(new ItemStack(Items.EMERALD), PostcardItem.getPostcard(list[i], false), 16, 10, 0.05F));
                }
            }
        }
        return ActionResult.PASS;
    }
}