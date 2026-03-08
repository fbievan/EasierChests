package de.guntram.mcmod.easierchests.mixins;

import de.guntram.mcmod.easierchests.ConfigurationHandler;
import de.guntram.mcmod.easierchests.EasierChests;
import de.guntram.mcmod.easierchests.ExtendedGuiChest;
import de.guntram.mcmod.easierchests.interfaces.SlotClicker;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Click;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.lwjgl.glfw.GLFW;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public abstract class AbstractContainerScreenMixin extends Screen implements SlotClicker {

    @Shadow protected void onMouseClick(Slot slot, int invSlot, int button, SlotActionType slotActionType) {}
    @Shadow @Final protected ScreenHandler handler;
    @Shadow protected int x, y, backgroundWidth, backgroundHeight;

    protected AbstractContainerScreenMixin() { super(null); }

    @Override
    public void EasierChests$onMouseClick(Slot slot, int invSlot, int button, SlotActionType slotActionType) {
        this.onMouseClick(slot, invSlot, button, slotActionType);
    }

    @Override
    public int EasierChests$getPlayerInventoryStartIndex() {
        if (handler instanceof PlayerScreenHandler) {
            return 9;
        } else {
            return this.handler.slots.size()-36;
        }
    }

    @Override
    public int EasierChests$playerInventoryIndexFromSlotIndex(int slot) {
        int firstSlot = EasierChests$getPlayerInventoryStartIndex();
        if (slot < firstSlot) {
            return -1;
        } else if (slot < firstSlot + 27) {
            return slot - firstSlot + 9;
        } else {
            return slot - firstSlot - 27;
        }
    }

    @Override
    public int EasierChests$slotIndexfromPlayerInventoryIndex(int slot) {
        int firstSlot = EasierChests$getPlayerInventoryStartIndex();
        if (slot < 9) {
            return slot + firstSlot + 27;
        } else {
            return slot + firstSlot - 9;
        }
    }

    @Inject(method="init", at=@At("RETURN"))
    public void EasierChests$registerMouseHandler(CallbackInfo ci) {
        ScreenMouseEvents.allowMouseClick((Screen)(Object)this).register(
            (screen, click) -> {
                double mouseX = click.x();
                double mouseY = click.y();
                if (mouseX >= x+backgroundWidth && mouseX <= x+backgroundWidth+18) {
                    HandledScreen HSthis = (HandledScreen)(Object)this;
                    if (mouseY >= y+backgroundHeight-30-3*18 && mouseY < y+backgroundHeight-30-2*18) {
                        ExtendedGuiChest.sortInventory(this, false, MinecraftClient.getInstance().player.getInventory());
                        return false;
                    } else if (isSupportedScreenHandler(handler)) {
                        if (mouseY >= y+backgroundHeight-30-3*18 && mouseY < y+backgroundHeight-30-1*18) {
                            ExtendedGuiChest.moveMatchingItems(HSthis, false);
                            return false;
                        } else if (mouseY > y+17 && mouseY < y+17+18) {
                            ExtendedGuiChest.sortInventory(this, true, handler.getSlot(0).inventory);
                            return false;
                        } else if (mouseY > y+17+18 && mouseY < y+17+36) {
                            ExtendedGuiChest.moveMatchingItems(HSthis, true);
                            return false;
                        }
                    }
                }
                return true;
            }
        );
        ScreenKeyboardEvents.allowKeyPress((Screen)(Object)this).register(
            (screen, input) -> {
                HandledScreen acScreen = (HandledScreen)(Object)this;
                if (EasierChests.keySortPlInv.matchesKey(input)) {
                    ExtendedGuiChest.sortInventory(this, false, MinecraftClient.getInstance().player.getInventory());
                    return false;
                } else if (EasierChests.keyMoveToChest.matchesKey(input)
                        && isSupportedScreenHandler(handler)) {
                    ExtendedGuiChest.moveMatchingItems(acScreen, false);
                    return false;
                } else if (EasierChests.keySortChest.matchesKey(input)) {
                    ExtendedGuiChest.sortInventory(this, true, handler.getSlot(0).inventory);
                    return false;
                } else if (EasierChests.keyMoveToPlInv.matchesKey(input)) {
                    ExtendedGuiChest.moveMatchingItems(acScreen, true);
                    return false;
                } else if (EasierChests.keySearchBox.matchesKey(input)) {
                    ConfigurationHandler.toggleSearchBox();
                    return false;
                }
                return true;
            }
        );
    }

    @Inject(method="drawSlot", at=@At("RETURN"))
    public void EasierChests$DrawSlotIndex(DrawContext context, Slot slot, int slotX, int slotY, CallbackInfo ci) {
        if (hasAltDown()) {
            context.drawText(this.textRenderer, Integer.toString(slot.id), slotX, slotY, 0x808090, false);
        }
    }

    @Inject(method="render", at=@At("RETURN"))
    public void EasierChests$renderSpecialButtons(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        Screen me = this;
        HandledScreen acScreen = (HandledScreen) me;
        ExtendedGuiChest.drawPlayerInventoryBroom(context, acScreen, x+backgroundWidth, y+backgroundHeight-30-3*18, mouseX, mouseY);
        if (isSupportedScreenHandler(handler)) {
            ExtendedGuiChest.drawPlayerInventoryAllUp(context, acScreen, x+backgroundWidth, y+backgroundHeight-30-2*18, mouseX, mouseY);
            ExtendedGuiChest.drawChestInventoryBroom(context, acScreen, x+backgroundWidth, y+17, mouseX, mouseY);
            ExtendedGuiChest.drawChestInventoryAllDown(context, acScreen, x+this.backgroundWidth, y+17+18, mouseX, mouseY);
        }
    }

    private static boolean hasAltDown() {
        long win = MinecraftClient.getInstance().getWindow().getHandle();
        return GLFW.glfwGetKey(win, GLFW.GLFW_KEY_LEFT_ALT) == GLFW.GLFW_PRESS
            || GLFW.glfwGetKey(win, GLFW.GLFW_KEY_RIGHT_ALT) == GLFW.GLFW_PRESS;
    }

    public boolean isSupportedScreenHandler(ScreenHandler handler) {
        if (handler instanceof GenericContainerScreenHandler || handler instanceof ShulkerBoxScreenHandler) {
            return true;
        }
        if (handler.getClass().getSimpleName().equals("BackpackScreenHandler")) {
            return true;
        }
        return false;
    }
}
