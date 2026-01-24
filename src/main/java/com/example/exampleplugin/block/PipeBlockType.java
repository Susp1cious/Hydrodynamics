package com.example.exampleplugin.block;

import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.connectedblocks.ConnectedBlockRuleSet;
import com.hypixel.hytale.server.core.universe.world.connectedblocks.ConnectedBlocksUtil;

import java.util.Optional;

public class PipeBlockType extends BlockType {

    float currentFlowRate;
    float currentFluidLevel;

    float maxFlowRate;
    float maxFluidLevel;

    class RuleSet extends ConnectedBlockRuleSet {

        @Override
        public boolean onlyUpdateOnPlacement() {
            return false;
        }

        @Override
        public Optional<ConnectedBlocksUtil.ConnectedBlockResult> getConnectedBlockType(
                World world,
                Vector3i vector3i,
                BlockType blockType,
                int i,
                Vector3i vector3i1,
                boolean b) {
            return null;
            //return Optional.of(new ConnectedBlocksUtil.ConnectedBlockResult(// UPDATE HERE));
        }
    }

    public PipeBlockType() {
        super();
    }
}
