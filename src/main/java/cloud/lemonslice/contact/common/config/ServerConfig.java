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
        public static ForgeConfigSpec.IntValue ticksToAnotherWorld;

        private static void load(ForgeConfigSpec.Builder builder)
        {
            builder.push("Mail");
            postalSpeed = builder.comment("The postal speed per block. (ticks)")
                    .defineInRange("PostalSpeed", 4, 0, 1200);
            ticksToAnotherWorld = builder.comment("The extra postal time needed to deliver to another world. (ticks)")
                    .defineInRange("TicksToAnotherWorld", 1200, 0, 12000);
            builder.pop();
        }
    }
}

