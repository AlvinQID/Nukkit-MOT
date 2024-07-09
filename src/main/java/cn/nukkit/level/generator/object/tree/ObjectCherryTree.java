package cn.nukkit.level.generator.object.tree;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockWood;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import lombok.var;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.BlockID.CHERRY_LEAVES;
import static cn.nukkit.block.BlockID.CHERRY_LOG;

public class ObjectCherryTree extends TreeGenerator {

    protected Block LOG_Y_AXIS = Block.get(CHERRY_LOG, BlockWood.faces[BlockFace.DOWN.getIndex()]);
    protected Block LOG_X_AXIS = Block.get(CHERRY_LOG, BlockWood.faces[BlockFace.WEST.getIndex()]);
    protected Block LOG_Z_AXIS = Block.get(CHERRY_LOG, BlockWood.faces[BlockFace.NORTH.getIndex()]);
    protected Block LEAVES = Block.get(CHERRY_LEAVES, 0);

    @Override
    public boolean generate(ChunkManager level, NukkitRandom rand, Vector3 position) {
        final int x = position.getFloorX();
        final int y = position.getFloorY();
        final int z = position.getFloorZ();

        final var isBigTree = rand.nextBoolean();
        if (isBigTree) {
            var ok = generateBigTree(level, rand, x, y, z);
            if (ok) return true;
        }
        return generateSmallTree(level, rand, x, y, z);
    }

    protected boolean generateBigTree(ChunkManager level, @NotNull NukkitRandom rand, final int x, final int y, final int z) {
        final int mainTrunkHeight = (rand.nextBoolean() ? 1 : 0) + 10;

        if (!canPlaceObject(level, mainTrunkHeight, x, y, z)) return false;

        var growOnXAxis = rand.nextBoolean();
        int xMultiplier = growOnXAxis ? 1 : 0;
        int zMultiplier = growOnXAxis ? 0 : 1;

        final int leftSideTrunkLength = rand.nextRange(2, 4);
        final int leftSideTrunkHeight = rand.nextRange(3, 5);
        final int leftSideTrunkStartY = rand.nextRange(4, 5);

        if (!canPlaceObject(level, leftSideTrunkHeight, x - leftSideTrunkLength * xMultiplier,
                y + leftSideTrunkStartY, z - leftSideTrunkLength * zMultiplier)) {
            growOnXAxis = !growOnXAxis;
            xMultiplier = growOnXAxis ? 1 : 0;
            zMultiplier = growOnXAxis ? 0 : 1;
            if (!canPlaceObject(level, leftSideTrunkHeight, x - leftSideTrunkLength * xMultiplier,
                    y + leftSideTrunkStartY, z - leftSideTrunkLength * zMultiplier)) {
                return false;
            }
        }

        final int rightSideTrunkLength = rand.nextRange(2, 4);
        final int rightSideTrunkHeight = rand.nextRange(3, 5);
        final int rightSideTrunkStartY = rand.nextRange(4, 5);

        if (!canPlaceObject(level, rightSideTrunkHeight, x + rightSideTrunkLength * xMultiplier,
                y + rightSideTrunkStartY, z + rightSideTrunkLength * zMultiplier)) return false;

        this.setDirtAt(level, new BlockVector3(x, y - 1, z));

        // Generate main trunk
        for (int yy = 0; yy < mainTrunkHeight; ++yy) {
            this.setBlockAndNotifyAdequately(level, x, y + yy, z, LOG_Y_AXIS.getBlock());
        }
        // generate side trunks
        final var sideBlockState = growOnXAxis ? LOG_X_AXIS : LOG_Z_AXIS;
        // generate left-side trunk
        for (int xx = 1; xx <= leftSideTrunkLength; ++xx) {
            if (this.canGrowInto(level.getBlockIdAt(x - xx * xMultiplier, y + leftSideTrunkStartY, z - xx * zMultiplier)))
                this.setBlockAndNotifyAdequately(level, x - xx * xMultiplier, y + leftSideTrunkStartY, z - xx * zMultiplier, sideBlockState);
        }
        for (int yy = 1; yy < leftSideTrunkHeight; ++yy) {
            if (this.canGrowInto(level.getBlockIdAt(x - leftSideTrunkLength * xMultiplier,
                    y + leftSideTrunkStartY + yy, z - leftSideTrunkLength * zMultiplier)))
                this.setBlockAndNotifyAdequately(level, x - leftSideTrunkLength * xMultiplier, y + leftSideTrunkStartY + yy,
                        z - leftSideTrunkLength * zMultiplier, LOG_Y_AXIS);
        }
        // We just generated this above
        //       |
        // |     |     |
        // └-----|-----┘
        //       |
        // However, when start y == 4, minecraft generate trunk like this:
        //       |
        // └-┐   |   ┌-┘
        //   └---|---┘
        //       |
        if (leftSideTrunkStartY == 4) {
            var tmpX = x - leftSideTrunkLength * xMultiplier;
            var tmpY = y + leftSideTrunkStartY;
            var tmpZ = z - leftSideTrunkLength * zMultiplier;
            level.setBlockIdAt(tmpX, tmpY, tmpZ, 0);
            tmpX += xMultiplier;
            tmpY += 1;
            tmpZ += zMultiplier;
            if (this.canGrowInto(level.getBlockIdAt(tmpX, tmpY, tmpZ))) {
                this.setBlockAndNotifyAdequately(level, tmpX, tmpY, tmpZ, LOG_Y_AXIS);
            }
            tmpX -= xMultiplier;
            tmpZ -= zMultiplier;
            if (this.canGrowInto(level.getBlockIdAt(tmpX, tmpY, tmpZ))) {
                this.setBlockAndNotifyAdequately(level, tmpX, tmpY, tmpZ, sideBlockState);
            }
        }
        // generate right-side trunk
        for (int xx = 1; xx <= rightSideTrunkLength; ++xx) {
            if (this.canGrowInto(level.getBlockIdAt(x + xx * xMultiplier, y + rightSideTrunkStartY, z + xx * zMultiplier)))
                this.setBlockAndNotifyAdequately(level, x + xx * xMultiplier, y + rightSideTrunkStartY, z + xx * zMultiplier, sideBlockState);
        }
        for (int yy = 1; yy < rightSideTrunkHeight; ++yy) {
            if (this.canGrowInto(level.getBlockIdAt(x + rightSideTrunkLength * xMultiplier,
                    y + rightSideTrunkStartY + yy, z + rightSideTrunkLength * zMultiplier)))
                this.setBlockAndNotifyAdequately(level,
                        x + rightSideTrunkLength * xMultiplier,
                        y + rightSideTrunkStartY + yy,
                        z + rightSideTrunkLength * zMultiplier,
                        LOG_Y_AXIS
                );
        }
        if (rightSideTrunkStartY == 4) {
            var tmpX = x + rightSideTrunkLength * xMultiplier;
            var tmpY = y + rightSideTrunkStartY;
            var tmpZ = z + rightSideTrunkLength * zMultiplier;
            level.setBlockIdAt(tmpX, tmpY, tmpZ, 0);
            tmpX -= xMultiplier;
            tmpY += 1;
            tmpZ -= zMultiplier;
            if (this.canGrowInto(level.getBlockIdAt(tmpX, tmpY, tmpZ))) {
                this.setBlockAndNotifyAdequately(level, tmpX, tmpY, tmpZ, LOG_Y_AXIS);
            }
            tmpX += xMultiplier;
            tmpZ += zMultiplier;
            if (this.canGrowInto(level.getBlockIdAt(tmpX, tmpY, tmpZ))) {
                this.setBlockAndNotifyAdequately(level, tmpX, tmpY, tmpZ, sideBlockState);
            }
        }
        // generate main trunk leaves
        generateLeaves(level, rand, x, y + mainTrunkHeight + 1, z);
        // generate left-side trunk leaves
        generateLeaves(level, rand, x - leftSideTrunkLength * xMultiplier,
                y + leftSideTrunkStartY + leftSideTrunkHeight + 1, z - leftSideTrunkLength * zMultiplier);
        // generate right-side trunk leaves
        generateLeaves(level, rand, x + rightSideTrunkLength * xMultiplier,
                y + rightSideTrunkStartY + rightSideTrunkHeight + 1, z + rightSideTrunkLength * zMultiplier);
        return true;
    }

    protected boolean generateSmallTree(ChunkManager level, @NotNull NukkitRandom rand, final int x, final int y, final int z) {
        final int mainTrunkHeight = (rand.nextBoolean() ? 1 : 0) + 4;
        final int sideTrunkHeight = rand.nextRange(3, 5);

        if (!canPlaceObject(level, mainTrunkHeight + 1, x, y, z)) return false;

        var growDirection = rand.nextRange(0, 3);
        int xMultiplier = 0;
        int zMultiplier = 0;
        var canPlace = false;
        for (int i = 0; i < 4; i++) {
            growDirection = (growDirection + 1) % 4;
            switch (growDirection) {
                case 0:
                    xMultiplier = -1;
                    break;
                case 1:
                    xMultiplier = 1;
                    break;
                default:
                    xMultiplier = 0;
                    break;
            }
            switch (growDirection) {
                case 2:
                    zMultiplier = -1;
                    break;
                case 3:
                    zMultiplier = 1;
                    break;
                default:
                    zMultiplier = 0;
                    break;
            }
            if (canPlaceObject(level, sideTrunkHeight, x + xMultiplier * sideTrunkHeight, y,
                    z + zMultiplier * sideTrunkHeight)) {
                canPlace = true;
                break;
            }
        }
        if (!canPlace) {
            return false;
        }


        final var sideBlockState = xMultiplier == 0 ? LOG_Z_AXIS : LOG_X_AXIS;
        // Generate main trunk
        for (int yy = 0; yy < mainTrunkHeight; ++yy) {
            if (this.canGrowInto(level.getBlockIdAt(x, y + yy, z)))
                this.setBlockAndNotifyAdequately(level, x, y + yy, z, LOG_Y_AXIS);
        }
        // Generate side trunk
        // (└)-┐      <- if side trunk is 4 or more blocks high, do not place the last block
        //     └-┐    <- side trunk
        //       └-┐
        //         |  <- main trunk
        //         |
        for (int yy = 1; yy <= sideTrunkHeight; ++yy) {
            var tmpX = x + yy * xMultiplier;
            var tmpY = y + mainTrunkHeight + yy - 2;
            var tmpZ = z + yy * zMultiplier;
            if (this.canGrowInto(level.getBlockIdAt(tmpX, tmpY, tmpZ))) {
                this.setBlockAndNotifyAdequately(level, tmpX, tmpY, tmpZ, sideBlockState);
            }
            // if side trunk is 4 or 5 blocks high, do not place the last block
            if (yy == sideTrunkHeight - 1 && sideTrunkHeight > 3) {
                continue;
            }
            tmpY += 1;
            if (this.canGrowInto(level.getBlockIdAt(tmpX, tmpY, tmpZ))) {
                this.setBlockAndNotifyAdequately(level, tmpX, tmpY, tmpZ, LOG_Y_AXIS);
            }
        }

        // generate leaves
        generateLeaves(level, rand, x + sideTrunkHeight * xMultiplier, y + mainTrunkHeight + sideTrunkHeight,
                z + sideTrunkHeight * zMultiplier);

        return true;
    }

    static final int LEAVES_RADIUS = 4;

    public void generateLeaves(ChunkManager level, NukkitRandom rand, final int x, final int y, final int z) {
        for (int dy = -2; dy <= 2; dy++) {
            for (int dx = -LEAVES_RADIUS; dx <= LEAVES_RADIUS; dx++) {
                for (int dz = -LEAVES_RADIUS; dz <= LEAVES_RADIUS; dz++) {
                    var currentRadius = LEAVES_RADIUS - (Math.max(1, Math.abs(dy)));
                    if (dx * dx + dz * dz > currentRadius * currentRadius) continue;
                    var blockId = level.getBlockIdAt(x + dx, y + dy, z + dz);
                    if (blockId == 0 || blockId == BlockID.LEAVES || blockId == BlockID.LEAVES2 ||
                            blockId == BlockID.AZALEA_LEAVES || blockId == BlockID.AZALEA_LEAVES_FLOWERED) {
                        this.setBlockAndNotifyAdequately(level, x + dx, y + dy, z + dz, LEAVES);
                    }
                    if (dy == -2 && rand.nextRange(0, 2) == 0) {
                        blockId = level.getBlockIdAt(x + dx, y + dy - 1, z + dz);
                        if (blockId == 0 || blockId == BlockID.LEAVES || blockId == BlockID.LEAVES2 ||
                                blockId == BlockID.AZALEA_LEAVES || blockId == BlockID.AZALEA_LEAVES_FLOWERED) {
                            this.setBlockAndNotifyAdequately(level, x + dx, y + dy - 1, z + dz, LEAVES);
                        }
                    }
                }
            }
        }
    }

    public boolean canPlaceObject(ChunkManager level, int treeHeight, int x, int y, int z) {
        int radiusToCheck = 0;
        for (int yy = 0; yy < treeHeight + 3; ++yy) {
            if (yy == 1 || yy == treeHeight) {
                ++radiusToCheck;
            }
            for (int xx = -radiusToCheck; xx < (radiusToCheck + 1); ++xx) {
                for (int zz = -radiusToCheck; zz < (radiusToCheck + 1); ++zz) {
                    if (!this.canGrowInto(level.getBlockIdAt(x + xx, y + yy, z + zz))) {
                        return false;
                    }
                }
            }
        }

        return true;
    }
}
