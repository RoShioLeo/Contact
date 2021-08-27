package cloud.lemonslice.contact.common.command.arguments;

import cloud.lemonslice.contact.resourse.PostcardHandler;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

public class PostcardStyleArgument extends ResourceLocationArgument
{

    private static final DynamicCommandExceptionType POSTCARD_STYLE_NOT_FOUND = new DynamicCommandExceptionType(o -> new TranslationTextComponent("message.contact.command.style_not_found", o));

    public static PostcardStyleArgument postcardStyle()
    {
        return new PostcardStyleArgument();
    }

    public static ResourceLocation getPostcardStyleID(CommandContext<CommandSource> context, String name) throws CommandSyntaxException
    {
        ResourceLocation id = context.getArgument(name, ResourceLocation.class);
        if (PostcardHandler.POSTCARD_MANAGER.getPostcards().get(id) == null)
        {
            throw POSTCARD_STYLE_NOT_FOUND.create(id);
        }
        else
        {
            return id;
        }
    }
}
