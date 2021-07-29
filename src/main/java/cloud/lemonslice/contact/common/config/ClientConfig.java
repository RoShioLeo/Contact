package cloud.lemonslice.contact.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig
{

    protected ClientConfig(ForgeConfigSpec.Builder builder)
    {
        GUI.load(builder);
    }

    public static class GUI
    {
        public static ForgeConfigSpec.BooleanValue showNewMailToast;

        private static void load(ForgeConfigSpec.Builder builder)
        {
            builder.push("GUI");
            showNewMailToast = builder
                    .comment("Should show the toast when receive new mail")
                    .define("ShowNewMailToast", true);
            builder.pop();
        }
    }
}
