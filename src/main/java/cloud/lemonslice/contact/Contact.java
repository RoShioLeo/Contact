package cloud.lemonslice.contact;

import cloud.lemonslice.contact.client.ClientProxy;
import cloud.lemonslice.contact.client.color.block.BlockColorsRegistry;
import cloud.lemonslice.contact.client.color.item.ItemColorsRegistry;
import cloud.lemonslice.contact.common.CommonProxy;
import cloud.lemonslice.contact.common.block.BlockRegistry;
import cloud.lemonslice.contact.common.command.ContactCommand;
import cloud.lemonslice.contact.common.config.NormalConfigs;
import cloud.lemonslice.contact.common.container.ContainerTypeRegistry;
import cloud.lemonslice.contact.common.item.ItemRegistry;
import cloud.lemonslice.contact.common.tileentity.BlockEntityTypeRegistry;
import cloud.lemonslice.contact.network.SimpleNetworkHandler;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cloud.lemonslice.contact.common.item.ItemRegistry.MAIL;

@Mod("contact")
public final class Contact
{
    public static final CreativeModeTab ITEM_GROUP = new CreativeModeTab("contact")
    {
        @Override
        public ItemStack makeIcon()
        {
            return new ItemStack(MAIL.get());
        }
    };

    public static final String MODID = "contact";
    public static final CommonProxy PROXY = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);

    public Contact()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        MinecraftForge.EVENT_BUS.addListener(this::onCommandRegister);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, NormalConfigs.SERVER_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, NormalConfigs.COMMON_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, NormalConfigs.CLIENT_CONFIG);
        cloud.lemonslice.silveroak.network.SimpleNetworkHandler.init();
        BlockRegistry.BLOCK_REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
        ItemRegistry.ITEM_REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
        BlockEntityTypeRegistry.BLOCK_ENTITY_REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
        ContainerTypeRegistry.MENU_TYPE_REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        SimpleNetworkHandler.init();
    }

    private void doClientStuff(final FMLClientSetupEvent event)
    {
        ClientProxy.registerRenderType();
        ContainerTypeRegistry.clientInit();
        ClientProxy.bindTileEntityRenderer();
        BlockColorsRegistry.init();
        ItemColorsRegistry.init();
    }

    public void onCommandRegister(RegisterCommandsEvent event)
    {
        ContactCommand.register(event.getDispatcher());
    }
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
}
