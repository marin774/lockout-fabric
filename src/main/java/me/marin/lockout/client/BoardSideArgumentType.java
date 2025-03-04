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

public class BoardSideArgumentType implements ArgumentType<String> {

    final static List<String> BOARD_SIDES = List.of(Constants.BOARD_SIDE_LEFT, Constants.BOARD_SIDE_RIGHT);

    public static BoardSideArgumentType newInstance() {
        return new BoardSideArgumentType();
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        String s = reader.readString();
        if (!BOARD_SIDES.contains(s)) {
            throw new SimpleCommandExceptionType(Text.of("Invalid board side.")).createWithContext(reader);
        }
        return s;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(BOARD_SIDES, builder);
    }
}
