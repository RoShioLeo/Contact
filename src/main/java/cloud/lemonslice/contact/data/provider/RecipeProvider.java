package cloud.lemonslice.contact.data.provider;

import cloud.lemonslice.contact.common.block.BlockRegistry;
import cloud.lemonslice.contact.common.item.ItemRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.Items;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public final class RecipeProvider extends net.minecraft.data.RecipeProvider
{
    public RecipeProvider(DataGenerator generatorIn)
    {
        super(generatorIn);
    }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer)
    {
        ShapedRecipeBuilder.shapedRecipe(BlockRegistry.WHITE_MAILBOX).key('x', Tags.Items.INGOTS_IRON).key('/', Tags.Items.RODS_WOODEN).patternLine("xxx").patternLine("x x").patternLine(" / ").setGroup("mailbox").addCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(BlockRegistry.ORANGE_MAILBOX).key('*', Tags.Items.DYES_ORANGE).key('x', Tags.Items.INGOTS_IRON).key('/', Tags.Items.RODS_WOODEN).patternLine("xxx").patternLine("x*x").patternLine(" / ").setGroup("mailbox").addCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(BlockRegistry.MAGENTA_MAILBOX).key('*', Tags.Items.DYES_MAGENTA).key('x', Tags.Items.INGOTS_IRON).key('/', Tags.Items.RODS_WOODEN).patternLine("xxx").patternLine("x*x").patternLine(" / ").setGroup("mailbox").addCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(BlockRegistry.LIGHT_BLUE_MAILBOX).key('*', Tags.Items.DYES_LIGHT_BLUE).key('x', Tags.Items.INGOTS_IRON).key('/', Tags.Items.RODS_WOODEN).patternLine("xxx").patternLine("x*x").patternLine(" / ").setGroup("mailbox").addCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(BlockRegistry.YELLOW_MAILBOX).key('*', Tags.Items.DYES_YELLOW).key('x', Tags.Items.INGOTS_IRON).key('/', Tags.Items.RODS_WOODEN).patternLine("xxx").patternLine("x*x").patternLine(" / ").setGroup("mailbox").addCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(BlockRegistry.LIME_MAILBOX).key('*', Tags.Items.DYES_LIME).key('x', Tags.Items.INGOTS_IRON).key('/', Tags.Items.RODS_WOODEN).patternLine("xxx").patternLine("x*x").patternLine(" / ").setGroup("mailbox").addCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(BlockRegistry.PINK_MAILBOX).key('*', Tags.Items.DYES_PINK).key('x', Tags.Items.INGOTS_IRON).key('/', Tags.Items.RODS_WOODEN).patternLine("xxx").patternLine("x*x").patternLine(" / ").setGroup("mailbox").addCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(BlockRegistry.GRAY_MAILBOX).key('*', Tags.Items.DYES_GRAY).key('x', Tags.Items.INGOTS_IRON).key('/', Tags.Items.RODS_WOODEN).patternLine("xxx").patternLine("x*x").patternLine(" / ").setGroup("mailbox").addCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(BlockRegistry.LIGHT_GRAY_MAILBOX).key('*', Tags.Items.DYES_LIGHT_GRAY).key('x', Tags.Items.INGOTS_IRON).key('/', Tags.Items.RODS_WOODEN).patternLine("xxx").patternLine("x*x").patternLine(" / ").setGroup("mailbox").addCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(BlockRegistry.CYAN_MAILBOX).key('*', Tags.Items.DYES_CYAN).key('x', Tags.Items.INGOTS_IRON).key('/', Tags.Items.RODS_WOODEN).patternLine("xxx").patternLine("x*x").patternLine(" / ").setGroup("mailbox").addCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(BlockRegistry.PURPLE_MAILBOX).key('*', Tags.Items.DYES_PURPLE).key('x', Tags.Items.INGOTS_IRON).key('/', Tags.Items.RODS_WOODEN).patternLine("xxx").patternLine("x*x").patternLine(" / ").setGroup("mailbox").addCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(BlockRegistry.BLUE_MAILBOX).key('*', Tags.Items.DYES_BLUE).key('x', Tags.Items.INGOTS_IRON).key('/', Tags.Items.RODS_WOODEN).patternLine("xxx").patternLine("x*x").patternLine(" / ").setGroup("mailbox").addCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(BlockRegistry.BROWN_MAILBOX).key('*', Tags.Items.DYES_BROWN).key('x', Tags.Items.INGOTS_IRON).key('/', Tags.Items.RODS_WOODEN).patternLine("xxx").patternLine("x*x").patternLine(" / ").setGroup("mailbox").addCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(BlockRegistry.GREEN_MAILBOX).key('*', Tags.Items.DYES_GREEN).key('x', Tags.Items.INGOTS_IRON).key('/', Tags.Items.RODS_WOODEN).patternLine("xxx").patternLine("x*x").patternLine(" / ").setGroup("mailbox").addCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(BlockRegistry.RED_MAILBOX).key('*', Tags.Items.DYES_RED).key('x', Tags.Items.INGOTS_IRON).key('/', Tags.Items.RODS_WOODEN).patternLine("xxx").patternLine("x*x").patternLine(" / ").setGroup("mailbox").addCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(BlockRegistry.BLACK_MAILBOX).key('*', Tags.Items.DYES_BLACK).key('x', Tags.Items.INGOTS_IRON).key('/', Tags.Items.RODS_WOODEN).patternLine("xxx").patternLine("x*x").patternLine(" / ").setGroup("mailbox").addCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON)).build(consumer);

        ShapedRecipeBuilder.shapedRecipe(BlockRegistry.GREEN_POSTBOX).key('*', Tags.Items.DYES_GREEN).key('x', Tags.Items.INGOTS_IRON).key('+', Tags.Items.STONE).patternLine("xxx").patternLine("x*x").patternLine("+++").setGroup("postbox").addCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(BlockRegistry.RED_POSTBOX).key('*', Tags.Items.DYES_RED).key('x', Tags.Items.INGOTS_IRON).key('+', Tags.Items.STONE).patternLine("xxx").patternLine("x*x").patternLine("+++").setGroup("postbox").addCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON)).build(consumer);

        ShapedRecipeBuilder.shapedRecipe(ItemRegistry.WRAPPING_PAPER).key('*', Tags.Items.STRING).key('x', Items.PAPER).patternLine("*x*").patternLine("xxx").patternLine("*x*").setGroup("wrapping_paper").addCriterion("has_paper", hasItem(Items.PAPER)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(ItemRegistry.ENDER_WRAPPING_PAPER).addIngredient(ItemRegistry.WRAPPING_PAPER).addIngredient(Items.ENDER_PEARL).setGroup("wrapping_paper").addCriterion("has_ender_pearl", hasItem(Items.ENDER_PEARL)).build(consumer);
    }

    @Override
    public String getName()
    {
        return "contact Recipes";
    }
}
