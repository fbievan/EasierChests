package de.guntram.mcmod.debug.mixins;

import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClickSlotC2SPacket.class)
public class ClickWindowC2SPacketMixin {

    static private final Logger LOGGER = LoggerFactory.getLogger("EasierChests");

    @Inject(method = "<init>(IISBLnet/minecraft/screen/slot/SlotActionType;Lit/unimi/dsi/fastutil/ints/Int2ObjectMap;Lnet/minecraft/screen/sync/ItemStackHash;)V", at = @At("RETURN"))
    private void dumpC2SNewInfo(CallbackInfo ci) {
        ClickSlotC2SPacket self = (ClickSlotC2SPacket)(Object)this;
        LOGGER.debug("new ClickSlot C2S: syncid={}, slot={}, button={}, action={}", self.syncId(), self.slot(), self.button(), self.actionType());
    }
}
