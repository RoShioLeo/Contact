package cloud.lemonslice.contact;

import cloud.lemonslice.contact.common.block.BlockRegistry;
import cloud.lemonslice.contact.common.command.ContactCommand;
import cloud.lemonslice.contact.common.config.ContactConfig;
import cloud.lemonslice.contact.common.entity.EntityTypeRegistry;
import cloud.lemonslice.contact.common.handler.AddresseeSignInHandler;
import cloud.lemonslice.contact.common.handler.MailboxManager;
import cloud.lemonslice.contact.common.handler.WanderingTraderSaleHandler;
import cloud.lemonslice.contact.common.item.ItemRegistry;
import cloud.lemonslice.contact.common.screenhandler.ScreenHandlerTypeRegistry;
import cloud.lemonslice.contact.common.tileentity.BlockEntityTypeRegistry;
import cloud.lemonslice.contact.network.NetworkHandler;
import cloud.lemonslice.contact.network.VersionCheckHandler;
import cloud.lemonslice.contact.resourse.PostcardHandler;
import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cloud.lemonslice.contact.common.item.ItemRegistry.LETTER;
import static cloud.lemonslice.contact.resourse.PostcardHandler.POSTCARD_MANAGER;

public final class Contact implements ModInitializer
{
    public static final String MODID = "contact";
    public static final String NETWORK_VERSION = "1.0";
    //    public static final ItemGroup ITEM_GROUP = FabricItemGroup.builder(new Identifier(MODID, "tab")).icon(() -> new ItemStack(LETTER)).build();
    public static final RegistryKey<ItemGroup> ITEM_GROUP = RegistryKey.of(RegistryKeys.ITEM_GROUP, new Identifier(MODID, "tab"));

    private static final Logger LOGGER = LogManager.getLogger();

    public static void error(String format, Object... data)
    {
        Contact.LOGGER.log(Level.ERROR, String.format(format, data));
    }

    public static void warn(String format, Object... data)
    {
        Contact.LOGGER.log(Level.WARN, String.format(format, data));
    }

    public static void info(String format, Object... data)
    {
        Contact.LOGGER.log(Level.INFO, String.format(format, data));
    }

    public static Identifier getIdentifier(String id)
    {
        return new Identifier(MODID, id);
    }

    @Override
    public void onInitialize()
    {
        BlockRegistry.initBlocks();
        ItemRegistry.initItems();
        BlockEntityTypeRegistry.init();
        EntityTypeRegistry.init();
        NetworkHandler.init();
        VersionCheckHandler.registerServerMessage();
        ScreenHandlerTypeRegistry.init();
        CommandRegistrationCallback.EVENT.register(ContactCommand::register);
        ServerTickEvents.START_SERVER_TICK.register(MailboxManager::onServerTick);
        UseEntityCallback.EVENT.register(WanderingTraderSaleHandler::onPlayerRightClickEntity);
        ServerPlayConnectionEvents.JOIN.register(AddresseeSignInHandler::onPlayerLoggedIn);
        ServerPlayConnectionEvents.JOIN.register(PostcardHandler::onPlayerLoggedIn);
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(POSTCARD_MANAGER);
        MidnightConfig.init(MODID, ContactConfig.class);
        Registry.register(Registries.ITEM_GROUP, ITEM_GROUP, FabricItemGroup.builder()
                .displayName(Text.translatable("itemGroup.contact.tab"))
                .icon(() -> new ItemStack(LETTER))
                .entries(ItemRegistry::initPostcardStyles)
                .build());
//        ItemGroupEvents.modifyEntriesEvent(ITEM_GROUP).register(ItemRegistry::initPostcardStyles);
    }
}
