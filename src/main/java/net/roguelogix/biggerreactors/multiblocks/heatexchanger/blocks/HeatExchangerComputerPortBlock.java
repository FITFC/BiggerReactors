package net.roguelogix.biggerreactors.multiblocks.heatexchanger.blocks;


import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.roguelogix.biggerreactors.multiblocks.heatexchanger.tiles.HeatExchangerComputerPortTile;
import net.roguelogix.phosphophyllite.registry.RegisterBlock;

import javax.annotation.Nullable;

@RegisterBlock(name = "heat_exchanger_computer_port", tileEntityClass = HeatExchangerComputerPortTile.class)
public class HeatExchangerComputerPortBlock extends HeatExchangerBaseBlock {
    
    @RegisterBlock.Instance
    public static HeatExchangerComputerPortBlock INSTANCE;
    
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HeatExchangerComputerPortTile(pos, state);
    }
}
