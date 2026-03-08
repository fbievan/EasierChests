package de.guntram.mcmod.debug.mixins;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.class)
public class DebugBlockBreakingDeltaMixin {

    @Inject(method="calcBlockBreakingDelta", at=@At(value="HEAD"))
    public void debugBlockBreakDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos, CallbackInfoReturnable<Float> ci) {
        float hardness = state.getHardness(world, pos);
        float speed = player.getBlockBreakingSpeed(state);
        System.out.println("Player " + player.getName().getString()
                + " breaking block, hardness=" + hardness
                + " breakSpeed=" + speed);
    }
}
