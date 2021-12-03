package cloud.lemonslice.contact.common.config;

import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class ServerConfig
{
    protected ServerConfig(ForgeConfigSpec.Builder builder)
    {
        Mail.load(builder);
    }

    public static class Mail
    {
        public static ForgeConfigSpec.IntValue postalSpeed;
        public static ForgeConfigSpec.ConfigValue<List<String>> blacklist;
        public static ForgeConfigSpec.IntValue configVersion;

        private static void load(ForgeConfigSpec.Builder builder)
        {
            builder.push("Mail");
            postalSpeed = builder.comment("The postal speed per block. (ticks)")
                    .defineInRange("PostalSpeed", 4, 0, 10);
            builder.pop();

            builder.push("Contraband");
            blacklist = builder.comment("ID of contraband that cannot be packed into parcel. e.g. contact:parcel")
                    .define("Blacklist", Lists.newArrayList("contact:parcel",
                            "minecraft:shulker_box", "minecraft:white_shulker_box", "minecraft:orange_shulker_box", "minecraft:magenta_shulker_box",
                            "minecraft:light_blue_shulker_box", "minecraft:yellow_shulker_box", "minecraft:lime_shulker_box", "minecraft:pink_shulker_box",
                            "minecraft:gray_shulker_box", "minecraft:light_gray_shulker_box", "minecraft:cyan_shulker_box", "minecraft:purple_shulker_box",
                            "minecraft:blue_shulker_box", "minecraft:brown_shulker_box", "minecraft:green_shulker_box", "minecraft:red_shulker_box", "minecraft:black_shulker_box"));
            builder.pop();

            builder.push("Version");
            configVersion = builder.comment("Please do not change!")
                    .defineInRange("ConfigVersion", 1, 1, 1);
            builder.pop();
        }
    }
}

