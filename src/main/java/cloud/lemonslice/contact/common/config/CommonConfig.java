package cloud.lemonslice.contact.common.config;

import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class CommonConfig
{
    protected CommonConfig(ForgeConfigSpec.Builder builder)
    {
        Mail.load(builder);
    }

    public static class Mail
    {
        public static ForgeConfigSpec.ConfigValue<List<String>> blacklistID;
        public static ForgeConfigSpec.ConfigValue<List<String>> blacklistName;

        private static void load(ForgeConfigSpec.Builder builder)
        {
            builder.push("Contraband");
            blacklistID = builder.comment("ID of items that cannot be packed into parcel. e.g. contact:parcel")
                    .define("IDBlacklist", Lists.newArrayList("contact:parcel",
                            "minecraft:shulker_box", "minecraft:white_shulker_box", "minecraft:orange_shulker_box", "minecraft:magenta_shulker_box",
                            "minecraft:light_blue_shulker_box", "minecraft:yellow_shulker_box", "minecraft:lime_shulker_box", "minecraft:pink_shulker_box",
                            "minecraft:gray_shulker_box", "minecraft:light_gray_shulker_box", "minecraft:cyan_shulker_box", "minecraft:purple_shulker_box",
                            "minecraft:blue_shulker_box", "minecraft:brown_shulker_box", "minecraft:green_shulker_box", "minecraft:red_shulker_box", "minecraft:black_shulker_box"));
            builder.pop();
        }
    }
}

