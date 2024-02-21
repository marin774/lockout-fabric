package me.marin.lockout.client;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.marin.lockout.client.gui.BoardBuilderIO;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CustomBoardFileArgumentType implements ArgumentType<String> {

    public CustomBoardFileArgumentType() {}

    public static CustomBoardFileArgumentType newInstance() {
        return new CustomBoardFileArgumentType();
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        String s = reader.getRemaining();
        reader.setCursor(reader.getTotalLength());
        try {
            List<String> boardNames = BoardBuilderIO.INSTANCE.getSavedBoards();
            if (!boardNames.contains(s)) {
                throw new SimpleCommandExceptionType(Text.of("Invalid board name.")).createWithContext(reader);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        try {
            List<String> boardNames = BoardBuilderIO.INSTANCE.getSavedBoards();
            return CommandSource.suggestMatching(boardNames, builder);
        } catch (IOException e) {
            e.printStackTrace();
            return Suggestions.empty();
        }
    }

}
