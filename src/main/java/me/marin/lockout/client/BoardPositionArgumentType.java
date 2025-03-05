package me.marin.lockout.client;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.marin.lockout.Constants;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class BoardPositionArgumentType implements ArgumentType<String> {

    final static List<String> BOARD_POSITIONS = List.of(Constants.BOARD_POSITION_LEFT, Constants.BOARD_POSITION_RIGHT);

    public static BoardPositionArgumentType newInstance() {
        return new BoardPositionArgumentType();
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        String s = reader.readString();
        if (!BOARD_POSITIONS.contains(s)) {
            throw new SimpleCommandExceptionType(Text.of("Invalid board position.")).createWithContext(reader);
        }
        return s;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(BOARD_POSITIONS, builder);
    }
}
