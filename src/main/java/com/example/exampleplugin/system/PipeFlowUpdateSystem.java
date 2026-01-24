package com.example.exampleplugin.system;

import com.example.exampleplugin.component.FluidStorageComponent;
import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockFace;
import com.hypixel.hytale.server.core.modules.block.BlockModule.BlockStateInfo;
import com.hypixel.hytale.server.core.universe.world.SetBlockSettings;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class PipeFlowUpdateSystem extends EntityTickingSystem<ChunkStore> {

    private final ComponentType<ChunkStore, FluidStorageComponent> fluidStorageType;

    public PipeFlowUpdateSystem(ComponentType<ChunkStore, FluidStorageComponent> fluidStorageType) {
        super();
        this.fluidStorageType = fluidStorageType;
    }

    @Override
    public void tick(float dt, int index,
                     @NonNull ArchetypeChunk<ChunkStore> chunk,
                     @NonNull Store<ChunkStore> store,
                     @NonNull CommandBuffer<ChunkStore> commandBuffer) {

        BlockStateInfo stateInfo = chunk.getComponent(index, BlockStateInfo.getComponentType());
        WorldChunk wc = commandBuffer.getComponent(stateInfo.getChunkRef(), WorldChunk.getComponentType());

        int i = stateInfo.getIndex();
        int x = ChunkUtil.worldCoordFromLocalCoord(wc.getX(), ChunkUtil.xFromBlockInColumn(i));
        int y = ChunkUtil.yFromBlockInColumn(i);
        int z = ChunkUtil.worldCoordFromLocalCoord(wc.getZ(), ChunkUtil.zFromBlockInColumn(i));

        updateNeighborsAndOrigin(new Vector3i(x, y, z), wc.getWorld(), wc, dt);
    }
    /** Logic for transfering fluid levels between neighboring blocks.
     * For now the method has been naively implemented and it iterates through every neighboring block
     * instead of connected ones.
     * TODO: Implement ConnectedBlocks somehow. */
    void updateNeighborsAndOrigin(Vector3i originBlockCoords,
                         @NonNull World world,
                         WorldChunk wc, float dt) {

        Holder<ChunkStore> originBlockComponentHolder = world.getBlockComponentHolder(originBlockCoords.x, originBlockCoords.y, originBlockCoords.z);
        if (originBlockComponentHolder == null) {
            System.out.println("Null Exception currentBlockComponentHolder");
            return;
        }
        FluidStorageComponent originFluidStorageComponent = originBlockComponentHolder.getComponent(fluidStorageType);
        if (originFluidStorageComponent == null) {
            System.out.println("Null Exception currentFluidStorageComponent");
            return;
        }

float originFluidLevel = originFluidStorageComponent.getCurrentStorage();
        float totalOriginTransfer = 0f;
        
        for (Vector3i neighborBlockCoords : getBlockFaces(originBlockCoords)) {
            Holder<ChunkStore> neighborBlockComponentHolder = world.getBlockComponentHolder(neighborBlockCoords.x, neighborBlockCoords.y, neighborBlockCoords.z);
            if (neighborBlockComponentHolder == null) {
                // System.out.println("Null Exception blockComponentHolder");
                continue;
            }
            FluidStorageComponent neighborFluidStorageComponent = neighborBlockComponentHolder.getComponent(fluidStorageType);
            if (neighborFluidStorageComponent == null) {
                System.out.println("Null Exception fluidStorageComponent");
                continue;
            }

            float neighborFluidLevel = neighborFluidStorageComponent.getCurrentStorage();

            // Fluid Transfer Logic
            float difference = originFluidLevel - neighborFluidLevel;
            float transferRate = 1f;    // Sets min transfer rate
            float transferAmount = transferRate * difference * dt;
            totalOriginTransfer += transferAmount;
            
            float newNeighborLevel = neighborFluidLevel + transferAmount;
            
            // Update Texture
            int valB = neighborFluidStorageComponent.setCurrentStorage(newNeighborLevel).toTen();
            updateBlockState(neighborBlockCoords, "pipe" + valB, wc, world);
        }
        
        float newOriginLevel = originFluidLevel - totalOriginTransfer;
        int valA = originFluidStorageComponent.setCurrentStorage(newOriginLevel).toTen();
        updateBlockState(originBlockCoords, "pipe" + valA, wc, world);
    }
    /** Helper function for getting the coordinates of the neighboring blocks. */
    Vector3i[] getBlockFaces(Vector3i origin) {
        Vector3i[] result = new Vector3i[6];
        for(int i = 0; i < 6; i++) {
            result[i] = origin.clone().add(BlockFace.values()[i].getDirection());
        }
        return result;
    }
    /** Helper function for changing the block texture. */
    void updateBlockState(Vector3i currentBlockCoords, String state, WorldChunk wc, World world) {
        world.execute(() -> {
            wc.setBlock(
                    currentBlockCoords.x,
                    currentBlockCoords.y,
                    currentBlockCoords.z,
                    wc.getBlockType(currentBlockCoords).getBlockForState(state),
                    SetBlockSettings.NO_SEND_PARTICLES
                        | SetBlockSettings.NO_SEND_AUDIO
            );
        });
    }
    @Override
    public @Nullable Query<ChunkStore> getQuery() {
        return fluidStorageType;
    }
}
