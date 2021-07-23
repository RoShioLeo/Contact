package cloud.lemonslice.intercourse;

import cloud.lemonslice.intercourse.client.ClientProxy;
import cloud.lemonslice.intercourse.client.color.block.BlockColorsRegistry;
import cloud.lemonslice.intercourse.client.color.item.ItemColorsRegistry;
import cloud.lemonslice.intercourse.common.CommonProxy;
import cloud.lemonslice.intercourse.common.block.BlockRegistry;
import cloud.lemonslice.intercourse.common.capability.CapabilityRegistry;
import cloud.lemonslice.intercourse.common.config.NormalConfigs;
import cloud.lemonslice.intercourse.common.container.ContainerTypeRegistry;
import cloud.lemonslice.intercourse.common.item.ItemRegistry;
import cloud.lemonslice.intercourse.common.tileentity.TileEntityTypeRegistry;
import cloud.lemonslice.intercourse.network.SimpleNetworkHandler;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
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

import static cloud.lemonslice.intercourse.common.item.ItemRegistry.MAIL;

@Mod("intercourse")
public final class Intercourse
{
    private static final Logger LOGGER = LogManager.getLogger();

    public static final String MODID = "intercourse";
    public static final CommonProxy PROXY = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);

    public Intercourse()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, NormalConfigs.SERVER_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, NormalConfigs.CLIENT_CONFIG);
        new BlockRegistry();
        new ItemRegistry();
        new TileEntityTypeRegistry();
        new ContainerTypeRegistry();
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        CommonProxy.registerCompostable();
        CommonProxy.registerFireInfo();
        SimpleNetworkHandler.init();
        CapabilityRegistry.init();
    }

    private void doClientStuff(final FMLClientSetupEvent event)
    {
        ClientProxy.registerRenderType();
        ContainerTypeRegistry.clientInit();
        ClientProxy.bindTileEntityRenderer();
        BlockColorsRegistry.init();
        ItemColorsRegistry.init();
    }

    public static void error(String format, Object... data)
    {
        Intercourse.LOGGER.log(Level.ERROR, String.format(format, data));
    }

    public static void warn(String format, Object... data)
    {
        Intercourse.LOGGER.log(Level.WARN, String.format(format, data));
    }

    public static void info(String format, Object... data)
    {
        Intercourse.LOGGER.log(Level.INFO, String.format(format, data));
    }

    public static final ItemGroup ITEM_GROUP = new ItemGroup("intercourse")
    {
        @Override
        public ItemStack createIcon()
        {
            return new ItemStack(MAIL);
        }
    };
}
