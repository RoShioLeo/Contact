package cloud.lemonslice.contact.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig
{
    protected ServerConfig(ForgeConfigSpec.Builder builder)
    {
        Mail.load(builder);
    }

    public static class Mail
    {
        public static ForgeConfigSpec.IntValue postalSpeed;

        private static void load(ForgeConfigSpec.Builder builder)
        {
            builder.push("Mail");
            postalSpeed = builder.comment("The postal speed per block. (ticks)")
                    .defineInRange("PostalSpeed", 4, 0, 10);
            builder.pop();
        }
    }
}

