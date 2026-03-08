package de.guntram.mcmod.debug.mixins;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class GuiActionConfirmDebugMixin {

    static private final Logger LOGGER = LoggerFactory.getLogger("EasierChests");

    @Inject(method="onInventory", at=@At("HEAD"))
    private void dumpInventoryInfo(InventoryS2CPacket packet, CallbackInfo ci) {
        LOGGER.debug("inventory: syncid={}, slotcount={}", packet.syncId(), packet.contents().size());
    }
}
