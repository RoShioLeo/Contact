package cloud.lemonslice.contact.common.handler;

import cloud.lemonslice.contact.common.capability.MailToBeSent;
import cloud.lemonslice.contact.common.capability.PlayerMailboxData;
import cloud.lemonslice.contact.common.config.ServerConfig;
import cloud.lemonslice.contact.common.item.PostcardItem;
import cloud.lemonslice.contact.common.tileentity.MailboxBlockEntity;
import cloud.lemonslice.contact.network.ActionMessage;
import cloud.lemonslice.contact.network.SimpleNetworkHandler;
import cloud.lemonslice.contact.resourse.PostcardHandler;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.List;
import java.util.UUID;

import static cloud.lemonslice.contact.common.capability.CapabilityRegistry.WORLD_MAILBOX_DATA;


@Mod.EventBusSubscriber
public final class MailboxManager
{
    private static final List<MailToBeSent> READY_TO_REMOVE = Lists.newArrayList();

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END)
        {
            ServerLifecycleHooks.getCurrentServer().getLevel(Level.OVERWORLD).getCapability(WORLD_MAILBOX_DATA).ifPresent(data ->
            {
                for (MailToBeSent mail : data.getData().mailList)
                {
                    mail.tick();
                    if (mail.isReady())
                    {
                        UUID uuid = mail.getUUID();
                        data.getData().addMailboxContents(uuid, mail.getContents());
                        Player player = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(uuid);
                        if (player != null)
                        {
                            player.displayClientMessage(new TranslatableComponent("message.contact.mailbox.new_mail"), false);
                        }
                        updateState(uuid, data.getData());
                        READY_TO_REMOVE.add(mail);
                    }
                }
                data.getData().mailList.removeAll(READY_TO_REMOVE);
                READY_TO_REMOVE.clear();
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        event.getPlayer().getCommandSenderWorld().getCapability(WORLD_MAILBOX_DATA).ifPresent(data ->
        {
            data.getData().nameToUUID.put(event.getPlayer().getName().getString(), event.getPlayer().getUUID());
            if (data.getData().uuidToContents.get(event.getPlayer().getUUID()) == null)
            {
                data.getData().resetMailboxContents(event.getPlayer().getUUID());
            }
            else
            {
                if (!data.getData().isMailboxEmpty(event.getPlayer().getUUID()))
                {
                    SimpleNetworkHandler.CHANNEL.sendTo(new ActionMessage(0), ((ServerPlayer) event.getPlayer()).connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
                }
                updateState(event.getPlayer().getUUID(), data.getData());
            }

        });
    }

    @SubscribeEvent
    public static void onPlayerRightClickEntity(PlayerInteractEvent.EntityInteract event)
    {
        if (!event.getWorld().isClientSide)
        {
            if (event.getTarget() instanceof WanderingTrader)
            {
                if (!event.getTarget().getTags().contains("SellPostcard"))
                {
                    WanderingTrader trader = (WanderingTrader) event.getTarget();
                    int i = event.getWorld().random.nextInt(PostcardHandler.POSTCARD_MANAGER.getPostcards().size());
                    trader.getTags().add("SellPostcard");
                    ResourceLocation[] list = PostcardHandler.POSTCARD_MANAGER.getPostcards().keySet().toArray(new ResourceLocation[0]);
                    trader.getOffers().add(0, new MerchantOffer(new ItemStack(Items.EMERALD), new ItemStack(Items.ENDER_PEARL), PostcardItem.getPostcard(list[i], true), 4, 10, 0.05F));
                    trader.getOffers().add(0, new MerchantOffer(new ItemStack(Items.EMERALD), PostcardItem.getPostcard(list[i], false), 4, 10, 0.05F));
                }
            }
        }
    }

    public static void updateState(UUID uuid, PlayerMailboxData data)
    {
        GlobalPos posData = data.getMailboxPos(uuid);
        if (posData != null)
        {
            updateState(ServerLifecycleHooks.getCurrentServer().getLevel(posData.dimension()), posData.pos());
        }
    }

    @SuppressWarnings("deprecation")
    public static void updateState(Level world, BlockPos pos)
    {
        if (world != null)
        {
            if (world.isAreaLoaded(pos, 1))
            {
                BlockEntity te = world.getBlockEntity(pos);
                if (te instanceof MailboxBlockEntity)
                {
                    ((MailboxBlockEntity) te).refreshStatus();
                }
            }
        }
    }

    public static int getDeliveryTicks(ResourceKey<Level> fromWorld, BlockPos fromPos, ResourceKey<Level> toWorld, BlockPos toPos)
    {
        int time = 0;
        if (fromWorld != toWorld)
        {
            time += 1200;
        }
        int distance = Math.abs(fromPos.getX() - toPos.getX()) + Math.abs(fromPos.getZ() - toPos.getZ());
        if (distance > 9000) distance = 9000;
        time += ServerConfig.Mail.postalSpeed.get() * distance;
        return time;
    }

    public static int getDeliveryTicks(GlobalPos fromPos, GlobalPos toPos)
    {
        return getDeliveryTicks(fromPos.dimension(), fromPos.pos(), toPos.dimension(), toPos.pos());
    }
}
