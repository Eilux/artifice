package com.swordglowsblue.artifice.mixin;

import com.swordglowsblue.artifice.impl.ArtificeResourcePackContainer;
import net.minecraft.client.gui.screen.resourcepack.AvailableResourcePackListWidget;
import net.minecraft.client.gui.screen.resourcepack.ResourcePackListWidget;
import net.minecraft.client.gui.screen.resourcepack.ResourcePackOptionsScreen;
import net.minecraft.client.gui.screen.resourcepack.SelectedResourcePackListWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(ResourcePackOptionsScreen.class)
public abstract class MixinResourcePackOptionsScreen {
    @Shadow private AvailableResourcePackListWidget availableList;
    @Shadow private SelectedResourcePackListWidget selectedList;
    private List<ResourcePackListWidget.ResourcePackEntry> hidden = new ArrayList<>();

    @Inject(method = "init", at = @At("RETURN"))
    private void hideNoDisplayPacks(CallbackInfo cbi) {
        List<ResourcePackListWidget.ResourcePackEntry> toRemove = new ArrayList<>();

        this.availableList.children().forEach(entry -> {
            if(!((ArtificeResourcePackContainer)entry.getPackContainer()).artifice_isOptional()) {
                toRemove.add(entry);
                System.out.println("Hidden pack "+entry.getPackContainer().getName());
            }
        });

        this.availableList.children().removeAll(toRemove);
        this.hidden.addAll(toRemove);
        toRemove.clear();

        this.selectedList.children().forEach(entry -> {
            if(!((ArtificeResourcePackContainer)entry.getPackContainer()).artifice_isOptional()) {
                toRemove.add(entry);
                System.out.println("Hidden pack "+entry.getPackContainer().getName());
            }
        });

        this.selectedList.children().removeAll(toRemove);
        this.hidden.addAll(toRemove);
    }

    @Inject(method = "method_19919", at = @At("HEAD"))
    private void ensureHiddenPacksAreSelected(CallbackInfo cbi) {
        this.selectedList.children().addAll(hidden);
    }
}
