package com.terraformersmc.mod_menu.mixin;

import com.terraformersmc.mod_menu.gui.ModsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(PauseScreen.class)
public abstract class MixinPauseScreen extends Screen {
    protected MixinPauseScreen(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void replaceStatsButton(CallbackInfo ci) {
        final int spacing = 24;

        var iterator = this.renderables.listIterator();

        var modsTranslatable = Component.translatable("fml.menu.mods");
        var returnToMenuTranslatable = Component.translatable("menu.returnToMenu");

        Button builtInModsButton = null;
        Button returnToMenuButton = null;

        while (iterator.hasNext()) {
            var widget = iterator.next();

            if (!(widget instanceof Button button))
                continue;

            var buttonText = button.getMessage();

            if (buttonText.equals(modsTranslatable)) {
                builtInModsButton = button;
                break;
            }

            if (buttonText.equals(returnToMenuTranslatable)) {
                returnToMenuButton = button;
            }
        }

        var replacedModsButtonBuilder = Button.builder(
                Component.translatable("fml.menu.mods"),
                btn -> Minecraft.getInstance().setScreen(new ModsScreen(this)));

        // This will run when Forge version is 47.2.0 or higher to override the built-in "Mods" button
        if (builtInModsButton != null) {
            builtInModsButton.visible = false;

            this.addRenderableWidget(
                    replacedModsButtonBuilder
                            .bounds(builtInModsButton.getX(), builtInModsButton.getY(),
                                    builtInModsButton.getWidth(), builtInModsButton.getHeight())
                            .build()
            );

            return;
        }

        // or lower than 47.2.0 for compatibility
        if (returnToMenuButton != null) {
            this.addRenderableWidget(
                    replacedModsButtonBuilder
                            .bounds(returnToMenuButton.getX(), returnToMenuButton.getY(),
                                    returnToMenuButton.getWidth(), returnToMenuButton.getHeight())
                            .build()
            );

            returnToMenuButton.setY(returnToMenuButton.getY() + spacing);
        }
    }
}
