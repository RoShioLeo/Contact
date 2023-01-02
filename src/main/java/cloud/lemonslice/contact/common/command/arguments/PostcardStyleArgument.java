package cloud.lemonslice.contact.common.command.arguments;

import cloud.lemonslice.contact.resourse.PostcardHandler;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class PostcardStyleArgument extends IdentifierArgumentType
{

    private static final DynamicCommandExceptionType POSTCARD_STYLE_NOT_FOUND = new DynamicCommandExceptionType(name -> Text.translatable("command.contact.style_not_found", name));

    public static PostcardStyleArgument postcardStyle()
    {
        return new PostcardStyleArgument();
    }

    public static Identifier getPostcardStyleID(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException
    {
        Identifier id = context.getArgument(name, Identifier.class);
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
