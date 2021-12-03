package cloud.lemonslice.contact.common.command.arguments;

import cloud.lemonslice.contact.resourse.PostcardHandler;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

public class PostcardStyleArgument extends ResourceLocationArgument
{

    private static final DynamicCommandExceptionType POSTCARD_STYLE_NOT_FOUND = new DynamicCommandExceptionType(name -> new TranslatableComponent("command.contact.style_not_found", name));

    public static PostcardStyleArgument postcardStyle()
    {
        return new PostcardStyleArgument();
    }

    public static ResourceLocation getPostcardStyleID(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException
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
