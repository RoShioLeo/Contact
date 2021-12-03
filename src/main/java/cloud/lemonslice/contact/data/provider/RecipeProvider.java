package cloud.lemonslice.contact.data.provider;

import cloud.lemonslice.contact.common.block.BlockRegistry;
import cloud.lemonslice.contact.common.item.ItemRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public final class RecipeProvider extends net.minecraft.data.recipes.RecipeProvider
{
    public RecipeProvider(DataGenerator generatorIn)
    {
        super(generatorIn);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer)
    {
        ShapedRecipeBuilder.shaped(BlockRegistry.WHITE_MAILBOX.get()).define('x', Tags.Items.INGOTS_IRON).define('/', Tags.Items.RODS_WOODEN).pattern("xxx").pattern("x x").pattern(" / ").group("mailbox").unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).save(consumer);
        ShapedRecipeBuilder.shaped(BlockRegistry.ORANGE_MAILBOX.get()).define('*', Tags.Items.DYES_ORANGE).define('x', Tags.Items.INGOTS_IRON).define('/', Tags.Items.RODS_WOODEN).pattern("xxx").pattern("x*x").pattern(" / ").group("mailbox").unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).save(consumer);
        ShapedRecipeBuilder.shaped(BlockRegistry.MAGENTA_MAILBOX.get()).define('*', Tags.Items.DYES_MAGENTA).define('x', Tags.Items.INGOTS_IRON).define('/', Tags.Items.RODS_WOODEN).pattern("xxx").pattern("x*x").pattern(" / ").group("mailbox").unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).save(consumer);
        ShapedRecipeBuilder.shaped(BlockRegistry.LIGHT_BLUE_MAILBOX.get()).define('*', Tags.Items.DYES_LIGHT_BLUE).define('x', Tags.Items.INGOTS_IRON).define('/', Tags.Items.RODS_WOODEN).pattern("xxx").pattern("x*x").pattern(" / ").group("mailbox").unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).save(consumer);
        ShapedRecipeBuilder.shaped(BlockRegistry.YELLOW_MAILBOX.get()).define('*', Tags.Items.DYES_YELLOW).define('x', Tags.Items.INGOTS_IRON).define('/', Tags.Items.RODS_WOODEN).pattern("xxx").pattern("x*x").pattern(" / ").group("mailbox").unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).save(consumer);
        ShapedRecipeBuilder.shaped(BlockRegistry.LIME_MAILBOX.get()).define('*', Tags.Items.DYES_LIME).define('x', Tags.Items.INGOTS_IRON).define('/', Tags.Items.RODS_WOODEN).pattern("xxx").pattern("x*x").pattern(" / ").group("mailbox").unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).save(consumer);
        ShapedRecipeBuilder.shaped(BlockRegistry.PINK_MAILBOX.get()).define('*', Tags.Items.DYES_PINK).define('x', Tags.Items.INGOTS_IRON).define('/', Tags.Items.RODS_WOODEN).pattern("xxx").pattern("x*x").pattern(" / ").group("mailbox").unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).save(consumer);
        ShapedRecipeBuilder.shaped(BlockRegistry.GRAY_MAILBOX.get()).define('*', Tags.Items.DYES_GRAY).define('x', Tags.Items.INGOTS_IRON).define('/', Tags.Items.RODS_WOODEN).pattern("xxx").pattern("x*x").pattern(" / ").group("mailbox").unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).save(consumer);
        ShapedRecipeBuilder.shaped(BlockRegistry.LIGHT_GRAY_MAILBOX.get()).define('*', Tags.Items.DYES_LIGHT_GRAY).define('x', Tags.Items.INGOTS_IRON).define('/', Tags.Items.RODS_WOODEN).pattern("xxx").pattern("x*x").pattern(" / ").group("mailbox").unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).save(consumer);
        ShapedRecipeBuilder.shaped(BlockRegistry.CYAN_MAILBOX.get()).define('*', Tags.Items.DYES_CYAN).define('x', Tags.Items.INGOTS_IRON).define('/', Tags.Items.RODS_WOODEN).pattern("xxx").pattern("x*x").pattern(" / ").group("mailbox").unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).save(consumer);
        ShapedRecipeBuilder.shaped(BlockRegistry.PURPLE_MAILBOX.get()).define('*', Tags.Items.DYES_PURPLE).define('x', Tags.Items.INGOTS_IRON).define('/', Tags.Items.RODS_WOODEN).pattern("xxx").pattern("x*x").pattern(" / ").group("mailbox").unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).save(consumer);
        ShapedRecipeBuilder.shaped(BlockRegistry.BLUE_MAILBOX.get()).define('*', Tags.Items.DYES_BLUE).define('x', Tags.Items.INGOTS_IRON).define('/', Tags.Items.RODS_WOODEN).pattern("xxx").pattern("x*x").pattern(" / ").group("mailbox").unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).save(consumer);
        ShapedRecipeBuilder.shaped(BlockRegistry.BROWN_MAILBOX.get()).define('*', Tags.Items.DYES_BROWN).define('x', Tags.Items.INGOTS_IRON).define('/', Tags.Items.RODS_WOODEN).pattern("xxx").pattern("x*x").pattern(" / ").group("mailbox").unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).save(consumer);
        ShapedRecipeBuilder.shaped(BlockRegistry.GREEN_MAILBOX.get()).define('*', Tags.Items.DYES_GREEN).define('x', Tags.Items.INGOTS_IRON).define('/', Tags.Items.RODS_WOODEN).pattern("xxx").pattern("x*x").pattern(" / ").group("mailbox").unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).save(consumer);
        ShapedRecipeBuilder.shaped(BlockRegistry.RED_MAILBOX.get()).define('*', Tags.Items.DYES_RED).define('x', Tags.Items.INGOTS_IRON).define('/', Tags.Items.RODS_WOODEN).pattern("xxx").pattern("x*x").pattern(" / ").group("mailbox").unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).save(consumer);
        ShapedRecipeBuilder.shaped(BlockRegistry.BLACK_MAILBOX.get()).define('*', Tags.Items.DYES_BLACK).define('x', Tags.Items.INGOTS_IRON).define('/', Tags.Items.RODS_WOODEN).pattern("xxx").pattern("x*x").pattern(" / ").group("mailbox").unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).save(consumer);

        ShapedRecipeBuilder.shaped(BlockRegistry.GREEN_POSTBOX.get()).define('*', Tags.Items.DYES_GREEN).define('x', Tags.Items.INGOTS_IRON).define('+', Tags.Items.STONE).pattern("xxx").pattern("x*x").pattern("+++").group("postbox").unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).save(consumer);
        ShapedRecipeBuilder.shaped(BlockRegistry.RED_POSTBOX.get()).define('*', Tags.Items.DYES_RED).define('x', Tags.Items.INGOTS_IRON).define('+', Tags.Items.STONE).pattern("xxx").pattern("x*x").pattern("+++").group("postbox").unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).save(consumer);

        ShapedRecipeBuilder.shaped(ItemRegistry.WRAPPING_PAPER.get()).define('*', Tags.Items.STRING).define('x', Items.PAPER).pattern("*x*").pattern("xxx").pattern("*x*").group("wrapping_paper").unlockedBy("has_paper", has(Items.PAPER)).save(consumer);
        ShapelessRecipeBuilder.shapeless(ItemRegistry.ENDER_WRAPPING_PAPER.get()).requires(ItemRegistry.WRAPPING_PAPER.get()).requires(Items.ENDER_PEARL).group("wrapping_paper").unlockedBy("has_ender_pearl", has(Items.ENDER_PEARL)).save(consumer);
    }

    @Override
    public String getName()
    {
        return "Contact Recipes";
    }
}
