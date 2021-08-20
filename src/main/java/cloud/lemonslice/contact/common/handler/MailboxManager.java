package cloud.lemonslice.contact.common.handler;

import cloud.lemonslice.contact.common.capability.MailToBeSent;
import cloud.lemonslice.contact.common.capability.PlayerMailboxData;
import cloud.lemonslice.contact.common.config.ServerConfig;
import cloud.lemonslice.contact.common.item.PostcardItem;
import cloud.lemonslice.contact.common.tileentity.MailboxTileEntity;
import cloud.lemonslice.contact.network.ActionMessage;
import cloud.lemonslice.contact.network.SimpleNetworkHandler;
import cloud.lemonslice.contact.resourse.PostcardHandler;
import com.google.common.collect.Lists;
import net.minecraft.entity.merchant.villager.WanderingTraderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.List;
import java.util.UUID;

import static cloud.lemonslice.contact.common.capability.CapabilityWorldPlayerMailboxData.WORLD_PLAYERS_DATA;

@Mod.EventBusSubscriber
public final class MailboxManager
{
    private static final List<MailToBeSent> READY_TO_REMOVE = Lists.newArrayList();

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END)
        {
            ServerLifecycleHooks.getCurrentServer().getWorld(World.OVERWORLD).getCapability(WORLD_PLAYERS_DATA).ifPresent(data ->
            {
                for (MailToBeSent mail : data.PLAYERS_DATA.mailList)
                {
                    mail.tick();
                    if (mail.isReady())
                    {
                        UUID uuid = mail.getUUID();
                        data.PLAYERS_DATA.addMailboxContents(uuid, mail.getContents());
                        PlayerEntity player = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByUUID(uuid);
                        if (player != null)
                        {
                            player.sendStatusMessage(new TranslationTextComponent("message.contact.mailbox.new_mail"), false);
                        }
                        updateState(uuid, data.PLAYERS_DATA);
                        READY_TO_REMOVE.add(mail);
                    }
                }
                data.PLAYERS_DATA.mailList.removeAll(READY_TO_REMOVE);
                READY_TO_REMOVE.clear();
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        event.getPlayer().getEntityWorld().getCapability(WORLD_PLAYERS_DATA).ifPresent(data ->
        {
            data.PLAYERS_DATA.nameToUUID.put(event.getPlayer().getName().getString(), event.getPlayer().getUniqueID());
            if (data.PLAYERS_DATA.uuidToContents.get(event.getPlayer().getUniqueID()) == null)
            {
                data.PLAYERS_DATA.resetMailboxContents(event.getPlayer().getUniqueID());
            }
            else
            {
                if (!data.PLAYERS_DATA.isMailboxEmpty(event.getPlayer().getUniqueID()))
                {
                    SimpleNetworkHandler.CHANNEL.sendTo(new ActionMessage(0), ((ServerPlayerEntity) event.getPlayer()).connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
                }
                updateState(event.getPlayer().getUniqueID(), data.PLAYERS_DATA);
            }

        });
    }

    @SubscribeEvent
    public static void onPlayerRightClickEntity(PlayerInteractEvent.EntityInteract event)
    {
        if (!event.getWorld().isRemote)
        {
            if (event.getTarget() instanceof WanderingTraderEntity)
            {
                if (!event.getTarget().getTags().contains("SellPostcard"))
                {
                    WanderingTraderEntity trader = (WanderingTraderEntity) event.getTarget();
                    int i = event.getWorld().rand.nextInt(PostcardHandler.POSTCARD_MANAGER.getPostcards().size());
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
            updateState(ServerLifecycleHooks.getCurrentServer().getWorld(posData.getDimension()), posData.getPos());
        }
    }

    public static void updateState(World world, BlockPos pos)
    {
        if (world != null)
        {
            if (world.isAreaLoaded(pos, 1))
            {
                TileEntity te = world.getTileEntity(pos);
                if (te instanceof MailboxTileEntity)
                {
                    ((MailboxTileEntity) te).refreshStatus();
                }
            }
        }
    }

    public static int getDeliveryTicks(RegistryKey<World> fromWorld, BlockPos fromPos, RegistryKey<World> toWorld, BlockPos toPos)
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
        return getDeliveryTicks(fromPos.getDimension(), fromPos.getPos(), toPos.getDimension(), toPos.getPos());
    }
}
