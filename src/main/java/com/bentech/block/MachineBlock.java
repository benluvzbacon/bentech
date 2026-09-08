package com.bentech.block;

import com.bentech.block.entity.AbstractMachineBlockEntity;
import com.bentech.block.entity.GeneratorBlockEntity;
import com.bentech.block.entity.ProcessingMachineBlockEntity;
import com.bentech.registry.ModBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * A machine block. Right-clicking opens the machine GUI (a screen) which shows
 * its inputs, output, energy buffer and process progress. Machines are fully
 * automatable with hoppers via {@link AbstractMachineBlockEntity}.
 */
public class MachineBlock extends BaseEntityBlock {

    private final MachineType type;

    public MachineBlock(BlockBehaviour.Properties properties, MachineType type) {
        super(properties);
        this.type = type;
    }

    public MachineType getType() {
        return type;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(props -> new MachineBlock(props, type));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        if (type.getKind() == MachineType.MachineKind.GENERATOR) {
            return new GeneratorBlockEntity(pos, state);
        }
        return new ProcessingMachineBlockEntity(pos, state);
    }

    // Renders the block as a normal 3D model (BaseEntityBlock defaults to INVISIBLE).
    @Override
    protected BlockBehaviour.RenderShape getRenderShape(BlockState state) {
        return BlockBehaviour.RenderShape.MODEL;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) {
            return null;
        }
        if (blockEntityType == ModBlockEntities.GENERATOR) {
            return createTickerHelper(blockEntityType, ModBlockEntities.GENERATOR, AbstractMachineBlockEntity::tick);
        }
        return createTickerHelper(blockEntityType, ModBlockEntities.PROCESSING, AbstractMachineBlockEntity::tick);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hit) {
        if (!level.isClientSide()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof AbstractMachineBlockEntity machine) {
                player.openMenu(machine);
            } else {
                return InteractionResult.PASS;
            }
        }
        return InteractionResult.SUCCESS;
    }
}
