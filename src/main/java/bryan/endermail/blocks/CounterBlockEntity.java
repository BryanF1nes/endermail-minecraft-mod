package bryan.endermail.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueOutput;

public class CounterBlockEntity extends BlockEntity {
    private int clicks = 0;
    private int ticksSinceLast = 0;

    public CounterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COUNTER_BLOCK_ENTITY, pos, state);
    }

    public int getClicks() {
        return this.clicks;
    }

    public void incrementClicks() {
        if (this.ticksSinceLast < 10)
            return;
        this.ticksSinceLast = 0;

        this.clicks++;
        this.setChanged();
    }

    @Override
    public void setChanged() {
        super.setChanged();

        if (level == null)
            return;

        BlockState state = getBlockState();
        level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_ALL);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        output.putInt("clicks", this.clicks);

        super.saveAdditional(output);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, CounterBlockEntity entity) {
        entity.ticksSinceLast++;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registryLookup) {
        return saveWithoutMetadata(registryLookup);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
