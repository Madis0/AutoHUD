package mod.crend.autohud.mixin.gui;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.systems.RenderSystem;
import mod.crend.autohud.AutoHud;
import mod.crend.autohud.component.Component;
import mod.crend.autohud.component.Hud;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.ScoreboardObjective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Mixin(value = InGameHud.class, priority = 800)
public class InGameHudMixin {

    // Hotbar
    @WrapOperation(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHotbar(FLnet/minecraft/client/util/math/MatrixStack;)V"
            )
    )
    private void autoHud$wrapHotbar(InGameHud instance, float tickDelta, MatrixStack matrixStack, Operation<Void> original) {
        if (AutoHud.targetHotbar) {
            Hud.preInject(matrixStack, Component.Hotbar);
        }
        original.call(instance, tickDelta, matrixStack);
        if (AutoHud.targetHotbar) {
            Hud.postInject(matrixStack);
        }
    }

    // Tooltip
    @WrapOperation(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHeldItemTooltip(Lnet/minecraft/client/util/math/MatrixStack;)V"
            )
    )
    private void autoHud$wrapTooltip(InGameHud instance, MatrixStack matrixStack, Operation<Void> original) {
        if (AutoHud.targetHotbar) {
            Hud.preInject(matrixStack, Component.Tooltip);
        }
        original.call(instance, matrixStack);
        if (AutoHud.targetHotbar) {
            Hud.postInject(matrixStack);
        }
    }

    // Hotbar items
    // We have to use matrix modification here to move the rendered items, as the y value is passed as an integer
    @Inject(method = "renderHotbarItem", at = @At(value = "HEAD"))
    private void autoHud$preHotbarItems(final int x, final int y, final float tickDelta, final PlayerEntity player, final ItemStack stack, final int seed, final CallbackInfo ci) {
        if (AutoHud.targetHotbar) {
            MatrixStack matrixStack = RenderSystem.getModelViewStack();
            Hud.preInject(matrixStack, Component.Hotbar);
            RenderSystem.applyModelViewMatrix();
        }
    }

    @Inject(method = "renderHotbarItem", at = @At(value = "RETURN"))
    private void autoHud$postHotbarItems(final int x, final int y, final float tickDelta, final PlayerEntity player, final ItemStack stack, final int seed, final CallbackInfo ci) {
        if (AutoHud.targetHotbar) {
            MatrixStack matrixStack = RenderSystem.getModelViewStack();
            Hud.postInject(matrixStack);
            RenderSystem.applyModelViewMatrix();
        }
    }

    // Experience Bar
    @WrapOperation(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/InGameHud;renderExperienceBar(Lnet/minecraft/client/util/math/MatrixStack;I)V"
            )
    )
    private void autoHud$wrapExperienceBar(InGameHud instance, MatrixStack matrixStack, int x, Operation<Void> original) {
        if (AutoHud.targetExperienceBar) {
            Hud.preInject(matrixStack, Component.ExperienceBar);
        }
        original.call(instance, matrixStack, x);
        if (AutoHud.targetExperienceBar) {
            Hud.postInject(matrixStack);
        }
    }

    // Status Bars
    @WrapOperation(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/InGameHud;renderStatusBars(Lnet/minecraft/client/util/math/MatrixStack;)V"
            )
    )
    private void autoHud$wrapStatusBars(InGameHud instance, MatrixStack matrixStack, Operation<Void> original) {
        if (AutoHud.targetStatusBars) {
            // Armor is the first rendered status bar in the vanilla renderer
            Hud.preInject(matrixStack, Component.Armor);
        }
        original.call(instance, matrixStack);
        if (AutoHud.targetStatusBars) {
            Hud.postInject(matrixStack);
        }
    }

    @Inject(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getProfiler()Lnet/minecraft/util/profiler/Profiler;", ordinal = 1))
    private void autoHud$postArmorBar(final MatrixStack matrixStack, final CallbackInfo ci) {
        if (AutoHud.targetStatusBars) {
            Hud.postInject(matrixStack);
            Hud.preInject(matrixStack, Component.Health);
        }
    }

    @Inject(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getProfiler()Lnet/minecraft/util/profiler/Profiler;", ordinal = 2))
    private void autoHud$postHealthBar(final MatrixStack matrixStack, final CallbackInfo ci) {
        if (AutoHud.targetStatusBars) {
            Hud.postInject(matrixStack);
            Hud.preInject(matrixStack, Component.Hunger);
        }
    }

    @Inject(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getProfiler()Lnet/minecraft/util/profiler/Profiler;", ordinal = 3))
    private void autoHud$postFoodBar(final MatrixStack matrixStack, final CallbackInfo ci) {
        if (AutoHud.targetStatusBars) {
            Hud.postInject(matrixStack);
            Hud.preInject(matrixStack, Component.Air);
        }
    }

    // Mount Health
    @WrapOperation(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/InGameHud;renderMountHealth(Lnet/minecraft/client/util/math/MatrixStack;)V"
            )
    )
    private void autoHud$wrapMountHealth(InGameHud instance, MatrixStack matrixStack, Operation<Void> original) {
        if (AutoHud.targetStatusBars) {
            Hud.preInject(matrixStack, Component.MountHealth);
        }
        original.call(instance, matrixStack);
        if (AutoHud.targetStatusBars) {
            Hud.postInject(matrixStack);
        }
    }

    // Mount Jump Bar
    @WrapOperation(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/InGameHud;renderMountJumpBar(Lnet/minecraft/client/util/math/MatrixStack;I)V"
            )
    )
    private void autoHud$wrapMountJumpBar(InGameHud instance, MatrixStack matrixStack, int x, Operation<Void> original) {
        if (AutoHud.targetStatusBars) {
            Hud.preInject(matrixStack, Component.MountJumpBar);
        }
        original.call(instance, matrixStack, x);
        if (AutoHud.targetStatusBars) {
            Hud.postInject(matrixStack);
        }
    }

    // Scoreboard
    @WrapOperation(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/InGameHud;renderScoreboardSidebar(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/scoreboard/ScoreboardObjective;)V"
            )
    )
    private void autoHud$wrapScoreboardSidebar(InGameHud instance, MatrixStack matrixStack, ScoreboardObjective objective, Operation<Void> original) {
        if (AutoHud.targetScoreboard) {
            Hud.preInject(matrixStack, Component.Scoreboard);
        }
        original.call(instance, matrixStack, objective);
        if (AutoHud.targetScoreboard) {
            Hud.postInject(matrixStack);
        }
    }

    // Status Effects
    @Inject(method = "renderStatusEffectOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffectInstance;getEffectType()Lnet/minecraft/entity/effect/StatusEffect;", ordinal = 0), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void autoHud$preEffect(MatrixStack matrices, CallbackInfo ci, Collection<StatusEffectInstance> collection, int i, int j, StatusEffectSpriteManager statusEffectSpriteManager, List<Runnable> list, Iterator<StatusEffectInstance> var7, StatusEffectInstance statusEffectInstance) {
        if (AutoHud.targetStatusEffects && Hud.shouldShowIcon(statusEffectInstance)) {
            Hud.preInject(matrices, Component.get(statusEffectInstance.getEffectType()));
        }
    }
    @Inject(method = "renderStatusEffectOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/StatusEffectSpriteManager;getSprite(Lnet/minecraft/entity/effect/StatusEffect;)Lnet/minecraft/client/texture/Sprite;"))
    private void autoHud$postEffect(MatrixStack matrices, CallbackInfo ci) {
        if (AutoHud.targetStatusEffects) {
            Hud.postInject(matrices);
        }
    }
    @Inject(method = "method_18620", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;drawSprite(Lnet/minecraft/client/util/math/MatrixStack;IIIIILnet/minecraft/client/texture/Sprite;)V"))
    private void autoHud$preSprite(Sprite sprite, float g, MatrixStack matrices, int n, int o, CallbackInfo ci) {
        if (AutoHud.targetStatusEffects) {
            Component component = Component.findBySprite(sprite);
            if (component != null) {
                Hud.preInject(matrices, component);
            } else {
                matrices.push();
            }
        }
    }
    @Inject(method = "method_18620", at = @At(value = "RETURN"))
    private void autoHud$postSprite(Sprite sprite, float g, MatrixStack matrices, int n, int o, CallbackInfo ci) {
        if (AutoHud.targetStatusEffects) {
            Hud.postInject(matrices);
        }
    }

    @Redirect(method = "renderStatusEffectOverlay", at = @At(value = "INVOKE", target="Lnet/minecraft/entity/effect/StatusEffectInstance;shouldShowIcon()Z"))
    private boolean autoHud$shouldShowIconProxy(StatusEffectInstance instance) {
        return Hud.shouldShowIcon(instance);
    }

    @Inject(method = "tick()V", at = @At(value = "TAIL"))
    private void autoHud$tickAutoHud(CallbackInfo ci) {
        Hud.tick();
    }

}
