package me.marin.lockout.mixin.client;

import me.marin.lockout.client.LockoutClient;
import me.marin.lockout.client.gui.BoardBuilderScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {

    @Inject(method="keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;setScreen(Lnet/minecraft/client/gui/screen/Screen;)V"), cancellable = true)
    public void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (LockoutClient.shouldOpenBoardBuilder) {
            LockoutClient.shouldOpenBoardBuilder = false;

            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null) {
                client.setScreen(new BoardBuilderScreen(Text.empty()));
            }
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

}
