package cloud.lemonslice.contact.data.provider;

import cloud.lemonslice.contact.common.block.BlockRegistry;
import cloud.lemonslice.contact.common.item.ItemRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;

import java.util.function.Consumer;

public final class RecipeProvider extends FabricRecipeProvider
{
    public RecipeProvider(FabricDataOutput dataOutput)
    {
        super(dataOutput);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> exporter)
    {
        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, BlockRegistry.WHITE_MAILBOX)
                .input('x', ConventionalItemTags.IRON_INGOTS)
                .input('/', Items.STICK)
                .pattern("xxx")
                .pattern("x x")
                .pattern(" / ")
                .group("mailbox")
                .criterion("has_iron", conditionsFromTag(ConventionalItemTags.IRON_INGOTS))
                .offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, BlockRegistry.ORANGE_MAILBOX)
                .input(ConventionalItemTags.ORANGE_DYES)
                .input(BlockRegistry.WHITE_MAILBOX)
                .group("dyed_mailbox")
                .criterion("has_dye", conditionsFromTag(ConventionalItemTags.ORANGE_DYES))
                .offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, BlockRegistry.MAGENTA_MAILBOX)
                .input(ConventionalItemTags.MAGENTA_DYES)
                .input(BlockRegistry.WHITE_MAILBOX)
                .group("dyed_mailbox")
                .criterion("has_dye", conditionsFromTag(ConventionalItemTags.MAGENTA_DYES))
                .offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, BlockRegistry.LIGHT_BLUE_MAILBOX)
                .input(ConventionalItemTags.LIGHT_BLUE_DYES)
                .input(BlockRegistry.WHITE_MAILBOX)
                .group("dyed_mailbox")
                .criterion("has_dye", conditionsFromTag(ConventionalItemTags.LIGHT_BLUE_DYES))
                .offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, BlockRegistry.YELLOW_MAILBOX)
                .input(ConventionalItemTags.YELLOW_DYES)
                .input(BlockRegistry.WHITE_MAILBOX)
                .group("dyed_mailbox")
                .criterion("has_dye", conditionsFromTag(ConventionalItemTags.YELLOW_DYES))
                .offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, BlockRegistry.LIME_MAILBOX)
                .input(ConventionalItemTags.LIME_DYES)
                .input(BlockRegistry.WHITE_MAILBOX)
                .group("dyed_mailbox")
                .criterion("has_dye", conditionsFromTag(ConventionalItemTags.LIME_DYES))
                .offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, BlockRegistry.PINK_MAILBOX)
                .input(ConventionalItemTags.PINK_DYES)
                .input(BlockRegistry.WHITE_MAILBOX)
                .group("dyed_mailbox")
                .criterion("has_dye", conditionsFromTag(ConventionalItemTags.PINK_DYES))
                .offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, BlockRegistry.GRAY_MAILBOX)
                .input(ConventionalItemTags.GRAY_DYES)
                .input(BlockRegistry.WHITE_MAILBOX)
                .group("dyed_mailbox")
                .criterion("has_dye", conditionsFromTag(ConventionalItemTags.GRAY_DYES))
                .offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, BlockRegistry.LIGHT_GRAY_MAILBOX)
                .input(ConventionalItemTags.LIGHT_GRAY_DYES)
                .input(BlockRegistry.WHITE_MAILBOX)
                .group("dyed_mailbox")
                .criterion("has_dye", conditionsFromTag(ConventionalItemTags.LIGHT_GRAY_DYES))
                .offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, BlockRegistry.CYAN_MAILBOX)
                .input(ConventionalItemTags.CYAN_DYES)
                .input(BlockRegistry.WHITE_MAILBOX)
                .group("dyed_mailbox")
                .criterion("has_dye", conditionsFromTag(ConventionalItemTags.CYAN_DYES))
                .offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, BlockRegistry.PURPLE_MAILBOX)
                .input(ConventionalItemTags.PURPLE_DYES)
                .input(BlockRegistry.WHITE_MAILBOX)
                .group("dyed_mailbox")
                .criterion("has_dye", conditionsFromTag(ConventionalItemTags.PURPLE_DYES))
                .offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, BlockRegistry.BLUE_MAILBOX)
                .input(ConventionalItemTags.BLUE_DYES)
                .input(BlockRegistry.WHITE_MAILBOX)
                .group("dyed_mailbox")
                .criterion("has_dye", conditionsFromTag(ConventionalItemTags.BLUE_DYES))
                .offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, BlockRegistry.BROWN_MAILBOX)
                .input(ConventionalItemTags.BROWN_DYES)
                .input(BlockRegistry.WHITE_MAILBOX)
                .group("dyed_mailbox")
                .criterion("has_dye", conditionsFromTag(ConventionalItemTags.BROWN_DYES))
                .offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, BlockRegistry.GREEN_MAILBOX)
                .input(ConventionalItemTags.GREEN_DYES)
                .input(BlockRegistry.WHITE_MAILBOX)
                .group("dyed_mailbox")
                .criterion("has_dye", conditionsFromTag(ConventionalItemTags.GREEN_DYES))
                .offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, BlockRegistry.RED_MAILBOX)
                .input(ConventionalItemTags.RED_DYES)
                .input(BlockRegistry.WHITE_MAILBOX)
                .group("dyed_mailbox")
                .criterion("has_dye", conditionsFromTag(ConventionalItemTags.RED_DYES))
                .offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, BlockRegistry.BLACK_MAILBOX)
                .input(ConventionalItemTags.BLACK_DYES)
                .input(BlockRegistry.WHITE_MAILBOX)
                .group("dyed_mailbox")
                .criterion("has_dye", conditionsFromTag(ConventionalItemTags.BLACK_DYES))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, BlockRegistry.GREEN_POSTBOX)
                .input('*', ConventionalItemTags.GREEN_DYES)
                .input('x', ConventionalItemTags.IRON_INGOTS)
                .input('+', Items.STONE)
                .pattern("xxx")
                .pattern("x*x")
                .pattern("+++")
                .group("postbox")
                .criterion("has_iron", conditionsFromTag(ConventionalItemTags.IRON_INGOTS))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, BlockRegistry.RED_POSTBOX)
                .input('*', ConventionalItemTags.RED_DYES)
                .input('x', ConventionalItemTags.IRON_INGOTS)
                .input('+', Items.STONE)
                .pattern("xxx")
                .pattern("x*x")
                .pattern("+++")
                .group("postbox")
                .criterion("has_iron", conditionsFromTag(ConventionalItemTags.IRON_INGOTS))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ItemRegistry.WRAPPING_PAPER)
                .input('*', Items.STRING)
                .input('x', Items.PAPER)
                .pattern("*x*")
                .pattern("xxx")
                .pattern("*x*")
                .group("wrapping_paper")
                .criterion(hasItem(Items.PAPER), conditionsFromItem(Items.PAPER))
                .offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ItemRegistry.ENDER_WRAPPING_PAPER)
                .input(ItemRegistry.WRAPPING_PAPER)
                .input(Items.ENDER_PEARL)
                .group("wrapping_paper")
                .criterion(hasItem(Items.ENDER_PEARL), conditionsFromItem(Items.ENDER_PEARL))
                .offerTo(exporter);
    }

    @Override
    public String getName()
    {
        return "Contact Recipes";
    }
}
