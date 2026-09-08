package com.bentech.block;

import com.bentech.block.entity.AbstractMachineBlockEntity;
import com.bentech.block.entity.GeneratorBlockEntity;
import com.bentech.block.entity.ProcessingMachineBlockEntity;
import com.bentech.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * A machine block. Right-clicking inserts the held item into the first empty
 * input slot (or, with shift, extracts the output). Machines are also fully
 * automatable with hoppers.
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
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        if (type.getKind() == MachineType.MachineKind.GENERATOR) {
            return new GeneratorBlockEntity(pos, state);
        }
        return new ProcessingMachineBlockEntity(pos, state);
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
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (!(level.getBlockEntity(pos) instanceof AbstractMachineBlockEntity machine)) {
            return InteractionResult.PASS;
        }
        ItemStack handStack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (player.isShiftKeyDown()) {
            ItemStack out = machine.getItem(AbstractMachineBlockEntity.SLOT_OUTPUT);
            if (!out.isEmpty()) {
                machine.setItem(AbstractMachineBlockEntity.SLOT_OUTPUT, ItemStack.EMPTY);
                if (!player.getInventory().add(out)) {
                    player.drop(out, false);
                }
            } else {
                player.displayClientMessage(Component.translatable("message.bentech.no_output"), true);
            }
            return InteractionResult.sidedSuccess(true);
        }
        if (!handStack.isEmpty()) {
            int slot = slotForInsert(machine);
            if (slot >= 0) {
                ItemStack toInsert = handStack.copy();
                toInsert.setCount(1);
                machine.setItem(slot, toInsert);
                handStack.shrink(1);
            } else {
                player.displayClientMessage(Component.translatable("message.bentech.input_full"), true);
            }
            return InteractionResult.sidedSuccess(true);
        }
        player.displayClientMessage(Component.literal(
                type.getDisplayName() + ": " + machine.getEnergy() + "/" + machine.getMaxEnergy() + " EU "
                        + (machine.isActive() ? "(active)" : "(idle)")), true);
        return InteractionResult.sidedSuccess(true);
    }

    private int slotForInsert(AbstractMachineBlockEntity machine) {
        if (machine.getItem(AbstractMachineBlockEntity.SLOT_INPUT_0).isEmpty()) {
            return AbstractMachineBlockEntity.SLOT_INPUT_0;
        }
        if (machine.getItem(AbstractMachineBlockEntity.SLOT_INPUT_1).isEmpty()) {
            return AbstractMachineBlockEntity.SLOT_INPUT_1;
        }
        return -1;
    }
}
