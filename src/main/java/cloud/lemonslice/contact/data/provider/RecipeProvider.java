package cloud.lemonslice.contact.data.provider;

import cloud.lemonslice.contact.common.block.BlockRegistry;
import cloud.lemonslice.contact.common.item.ItemRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public final class RecipeProvider extends net.minecraft.data.recipes.RecipeProvider
{
    public RecipeProvider(PackOutput packOutput)
    {
        super(packOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer)
    {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BlockRegistry.WHITE_MAILBOX.get())
                .define('x', Tags.Items.INGOTS_IRON)
                .define('/', Tags.Items.RODS_WOODEN)
                .pattern("xxx")
                .pattern("x x")
                .pattern(" / ")
                .group("mailbox")
                .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
                .save(consumer);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, BlockRegistry.ORANGE_MAILBOX.get())
                .requires(Tags.Items.DYES_ORANGE)
                .requires(BlockRegistry.WHITE_MAILBOX.get())
                .group("dyed_mailbox")
                .unlockedBy("has_dye", has(Tags.Items.DYES_ORANGE))
                .save(consumer);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, BlockRegistry.MAGENTA_MAILBOX.get())
                .requires(Tags.Items.DYES_MAGENTA)
                .requires(BlockRegistry.WHITE_MAILBOX.get())
                .group("dyed_mailbox")
                .unlockedBy("has_dye", has(Tags.Items.DYES_MAGENTA))
                .save(consumer);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, BlockRegistry.LIGHT_BLUE_MAILBOX.get())
                .requires(Tags.Items.DYES_LIGHT_BLUE)
                .requires(BlockRegistry.WHITE_MAILBOX.get())
                .group("dyed_mailbox")
                .unlockedBy("has_dye", has(Tags.Items.DYES_LIGHT_BLUE))
                .save(consumer);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, BlockRegistry.YELLOW_MAILBOX.get())
                .requires(Tags.Items.DYES_YELLOW)
                .requires(BlockRegistry.WHITE_MAILBOX.get())
                .group("dyed_mailbox")
                .unlockedBy("has_dye", has(Tags.Items.DYES_YELLOW))
                .save(consumer);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, BlockRegistry.LIME_MAILBOX.get())
                .requires(Tags.Items.DYES_LIME)
                .requires(BlockRegistry.WHITE_MAILBOX.get())
                .group("dyed_mailbox")
                .unlockedBy("has_dye", has(Tags.Items.DYES_LIME))
                .save(consumer);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, BlockRegistry.PINK_MAILBOX.get())
                .requires(Tags.Items.DYES_PINK)
                .requires(BlockRegistry.WHITE_MAILBOX.get())
                .group("dyed_mailbox")
                .unlockedBy("has_dye", has(Tags.Items.DYES_PINK))
                .save(consumer);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, BlockRegistry.GRAY_MAILBOX.get())
                .requires(Tags.Items.DYES_GRAY)
                .requires(BlockRegistry.WHITE_MAILBOX.get())
                .group("dyed_mailbox")
                .unlockedBy("has_dye", has(Tags.Items.DYES_GRAY))
                .save(consumer);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, BlockRegistry.LIGHT_GRAY_MAILBOX.get())
                .requires(Tags.Items.DYES_LIGHT_GRAY)
                .requires(BlockRegistry.WHITE_MAILBOX.get())
                .group("dyed_mailbox")
                .unlockedBy("has_dye", has(Tags.Items.DYES_LIGHT_GRAY))
                .save(consumer);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, BlockRegistry.CYAN_MAILBOX.get())
                .requires(Tags.Items.DYES_CYAN)
                .requires(BlockRegistry.WHITE_MAILBOX.get())
                .group("dyed_mailbox")
                .unlockedBy("has_dye", has(Tags.Items.DYES_CYAN))
                .save(consumer);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, BlockRegistry.PURPLE_MAILBOX.get())
                .requires(Tags.Items.DYES_PURPLE)
                .requires(BlockRegistry.WHITE_MAILBOX.get())
                .group("dyed_mailbox")
                .unlockedBy("has_dye", has(Tags.Items.DYES_PURPLE))
                .save(consumer);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, BlockRegistry.BLUE_MAILBOX.get())
                .requires(Tags.Items.DYES_BLUE)
                .requires(BlockRegistry.WHITE_MAILBOX.get())
                .group("dyed_mailbox")
                .unlockedBy("has_dye", has(Tags.Items.DYES_BLUE))
                .save(consumer);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, BlockRegistry.BROWN_MAILBOX.get())
                .requires(Tags.Items.DYES_BROWN)
                .requires(BlockRegistry.WHITE_MAILBOX.get())
                .group("dyed_mailbox")
                .unlockedBy("has_dye", has(Tags.Items.DYES_BROWN))
                .save(consumer);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, BlockRegistry.GREEN_MAILBOX.get())
                .requires(Tags.Items.DYES_GREEN)
                .requires(BlockRegistry.WHITE_MAILBOX.get())
                .group("dyed_mailbox")
                .unlockedBy("has_dye", has(Tags.Items.DYES_GREEN))
                .save(consumer);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, BlockRegistry.RED_MAILBOX.get())
                .requires(Tags.Items.DYES_RED)
                .requires(BlockRegistry.WHITE_MAILBOX.get())
                .group("dyed_mailbox")
                .unlockedBy("has_dye", has(Tags.Items.DYES_RED))
                .save(consumer);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, BlockRegistry.BLACK_MAILBOX.get())
                .requires(Tags.Items.DYES_BLACK)
                .requires(BlockRegistry.WHITE_MAILBOX.get())
                .group("dyed_mailbox")
                .unlockedBy("has_dye", has(Tags.Items.DYES_BLACK))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BlockRegistry.GREEN_POSTBOX.get())
                .define('*', Tags.Items.DYES_GREEN)
                .define('x', Tags.Items.INGOTS_IRON)
                .define('+', Tags.Items.STONE)
                .pattern("xxx")
                .pattern("x*x")
                .pattern("+++")
                .group("postbox")
                .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
                .save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BlockRegistry.RED_POSTBOX.get())
                .define('*', Tags.Items.DYES_RED)
                .define('x', Tags.Items.INGOTS_IRON)
                .define('+', Tags.Items.STONE)
                .pattern("xxx")
                .pattern("x*x")
                .pattern("+++")
                .group("postbox")
                .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemRegistry.WRAPPING_PAPER.get())
                .define('*', Tags.Items.STRING)
                .define('x', Items.PAPER)
                .pattern("*x*")
                .pattern("xxx")
                .pattern("*x*")
                .group("wrapping_paper")
                .unlockedBy("has_paper", has(Items.PAPER))
                .save(consumer);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemRegistry.ENDER_WRAPPING_PAPER.get())
                .requires(ItemRegistry.WRAPPING_PAPER.get())
                .requires(Items.ENDER_PEARL)
                .group("wrapping_paper")
                .unlockedBy("has_ender_pearl", has(Items.ENDER_PEARL))
                .save(consumer);
    }
}
