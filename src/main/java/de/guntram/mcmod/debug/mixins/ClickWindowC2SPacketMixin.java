package de.guntram.mcmod.debug.mixins;

import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClickSlotC2SPacket.class)
public class ClickWindowC2SPacketMixin {

    static private final Logger LOGGER = LoggerFactory.getLogger("EasierChests");

    @Inject(method = "<init>", at = @At("RETURN"))
    private void dumpC2SNewInfo(int syncid, int revision, int slot, int button, SlotActionType actionType,
            net.minecraft.item.ItemStack carriedStack, java.util.Map<Integer, net.minecraft.item.ItemStack> modifiedSlots,
            CallbackInfo ci) {
        LOGGER.debug("new ClickSlot C2S: syncid={}, slot={}, button={}, action={}", syncid, slot, button, actionType);
    }
}
