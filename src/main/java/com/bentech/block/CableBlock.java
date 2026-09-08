package com.bentech.block;

import com.bentech.block.entity.CableBlockEntity;
import com.bentech.registry.ModBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A power cable. It relays EU between adjacent machines and other cables so a
 * generator can power machines that are more than one block away. It renders
 * as a thin cross of wire (a {@link #CROSS} model), not as a solid block, so
 * it reads as a real cable and does not occlude its neighbours.
 */
public class CableBlock extends BaseEntityBlock {

    // A 3-axis cross: a thin wire running through the middle of the block.
    private static final VoxelShape CROSS = Shapes.or(
            Block.box(6, 0, 6, 10, 16, 10),   // vertical
            Block.box(0, 6, 6, 16, 10, 10),   // X axis
            Block.box(6, 6, 0, 10, 10, 16));  // Z axis

    public CableBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return CROSS;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return CROSS;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(CableBlock::new);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CableBlockEntity(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) {
            return null;
        }
        return createTickerHelper(blockEntityType, ModBlockEntities.CABLE, CableBlockEntity::tick);
    }
}
