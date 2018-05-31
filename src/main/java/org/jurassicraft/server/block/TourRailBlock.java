package org.jurassicraft.server.block;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jurassicraft.server.block.entity.TourRailBlockEntity;
import org.jurassicraft.server.tab.TabHandler;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static net.minecraft.util.EnumFacing.*;

public final class TourRailBlock extends Block {

    public static final PropertyEnum<TourRailBlock.EnumRailDirection> SHAPE = PropertyEnum.create("shape", TourRailBlock.EnumRailDirection.class);


    protected static final AxisAlignedBB FLAT_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D);
    protected static final AxisAlignedBB ASCENDING_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D);

    public TourRailBlock() {
        super(Material.CIRCUITS);
        this.setCreativeTab(TabHandler.BLOCKS);
        this.setHarvestLevel("pickaxe", 1);
        this.setHardness(1);
        this.setDefaultState(this.blockState.getBaseState().withProperty(SHAPE, EnumRailDirection.NORTH_SOUTH));
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return worldIn.getTileEntity(pos) instanceof TourRailBlockEntity ? state.withProperty(SHAPE, getRailDirection(worldIn, pos)) : this.getDefaultState();
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TourRailBlockEntity();
    }

    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return BlockFaceShape.UNDEFINED;
    }

    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return worldIn.getBlockState(pos.down()).isSideSolid(worldIn, pos.down(), EnumFacing.UP);
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        super.breakBlock(world, pos, state);
        if (!world.isRemote) {
            world.notifyNeighborsOfStateChange(pos, this, true);
            world.notifyNeighborsOfStateChange(pos.down(), this, true);
        }
    }

    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return NULL_AABB;
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        TourRailBlock.EnumRailDirection enumRailDirection = state.getBlock() == this ? getRailDirection(source, pos) : null;
        return enumRailDirection != null && enumRailDirection.isAscending() ? ASCENDING_AABB : FLAT_AABB;
    }

    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        this.updateDir(worldIn, pos, state, true);
        if (!worldIn.isRemote) {
            state.neighborChanged(worldIn, pos, this, pos);
        }
    }

    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (!worldIn.isRemote)
        {
            TourRailBlock.EnumRailDirection dir = getRailDirection(worldIn, pos);
            boolean flag = false;

            if (!worldIn.getBlockState(pos.down()).isSideSolid(worldIn, pos.down(), EnumFacing.UP))
            {
                flag = true;
            }

            if (dir == TourRailBlock.EnumRailDirection.ASCENDING_EAST && !worldIn.getBlockState(pos.east()).isSideSolid(worldIn, pos.east(), EnumFacing.UP))
            {
                flag = true;
            }
            else if (dir == TourRailBlock.EnumRailDirection.ASCENDING_WEST && !worldIn.getBlockState(pos.west()).isSideSolid(worldIn, pos.west(), EnumFacing.UP))
            {
                flag = true;
            }
            else if (dir == TourRailBlock.EnumRailDirection.ASCENDING_NORTH && !worldIn.getBlockState(pos.north()).isSideSolid(worldIn, pos.north(), EnumFacing.UP))
            {
                flag = true;
            }
            else if (dir == TourRailBlock.EnumRailDirection.ASCENDING_SOUTH && !worldIn.getBlockState(pos.south()).isSideSolid(worldIn, pos.south(), EnumFacing.UP))
            {
                flag = true;
            }

            if (flag && !worldIn.isAirBlock(pos))
            {
                this.dropBlockAsItem(worldIn, pos, state, 0);
                worldIn.setBlockToAir(pos);
            }
            else
            {
                this.updateState(state, worldIn, pos, blockIn);
            }
        }
    }

    public static TourRailBlock.EnumRailDirection getRailDirection(IBlockAccess world, BlockPos pos) {
        return world.getTileEntity(pos) instanceof  TourRailBlockEntity ? ((TourRailBlockEntity) world.getTileEntity(pos)).getDirection() : EnumRailDirection.NORTH_SOUTH;
    }

    protected void updateState(IBlockState state, World world, BlockPos pos, Block blockIn) {
        if (blockIn.getDefaultState().canProvidePower() && (new TourRailBlock.Rail(world, pos, state)).countAdjacentRails() == 3) {
            this.updateDir(world, pos, state, false);
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, SHAPE);
    }

    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return new ItemStack(BlockHandler.TOUR_RAIL);
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return this.getItem(world, pos, state);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(BlockHandler.TOUR_RAIL);
    }

    public static boolean isRailBlock(World worldIn, BlockPos pos)
    {
        return isRailBlock(worldIn.getBlockState(pos));
    }

    public static boolean isRailBlock(IBlockState state)
    {
        Block block = state.getBlock();
        return block instanceof TourRailBlock;
    }

    private IBlockState updateDir(World worldIn, BlockPos pos, IBlockState state, boolean initialPlacement)
    {
        return (new TourRailBlock.Rail(worldIn, pos, state)).place(worldIn.isBlockPowered(pos), initialPlacement).getBlockState();
    }

    public enum EnumRailDirection implements IStringSerializable {
        NORTH_SOUTH         ( 0,  0, -1,  0,  0,  1),
        EAST_WEST           (-1,  0,  0,  1,  0,  0),
        ASCENDING_EAST      (-1, -1,  0,  1,  0,  0),
        ASCENDING_WEST      (-1,  0,  0,  1, -1,  0),
        ASCENDING_NORTH     ( 0,  0, -1,  0, -1,  1),
        ASCENDING_SOUTH     ( 0, -1, -1,  0,  0,  1),
        SOUTH_EAST          ( 0,  0,  1,  1,  0,  0),
        SOUTH_WEST          ( 0,  0,  1, -1,  0,  0),
        NORTH_WEST          ( 0,  0, -1, -1,  0,  0),
        NORTH_EAST          ( 0,  0, -1,  1,  0,  0),

        DIAGONAL_NE_SW      ( 0.5F, 0, -0.5F, -0.5F, 0,  0.5F),
        DIAGONAL_NW_SE      ( 0.5F, 0, 0.5F,  -0.5F, 0, -0.5F),

        HORIZONTAL_NE       (EAST, EAST_WEST, DIAGONAL_NE_SW),
        HORIZONTAL_NW       (WEST, EAST_WEST, DIAGONAL_NW_SE),
        HORIZONTAL_SE       (EAST, EAST_WEST, DIAGONAL_NW_SE),
        HORIZONTAL_SW       (WEST, EAST_WEST, DIAGONAL_NE_SW),

        VERTICAL_NE         (NORTH, NORTH_SOUTH, DIAGONAL_NE_SW),
        VERTICAL_NW         (NORTH, NORTH_SOUTH, DIAGONAL_NW_SE),
        VERTICAL_SE         (SOUTH, NORTH_SOUTH, DIAGONAL_NW_SE),
        VERTICAL_SW         (SOUTH, NORTH_SOUTH, DIAGONAL_NE_SW);

        private final Type type;

        private float forwardX;
        private float forwardY;
        private float forwardZ;
        private float backwardsX;
        private float backwardsY;
        private float backwardsZ;

        private LinkedObjectRotation direction;
        private EnumRailDirection fallbackdirection;

        EnumRailDirection(float forwardX, float forward_y, float forward_z, float backwards_x, float backwards_y, float backwards_z) {
            this.forwardX = forwardX;
            this.forwardY = forward_y;
            this.forwardZ = forward_z;
            this.backwardsX = backwards_x;
            this.backwardsY = backwards_y;
            this.backwardsZ = backwards_z;
            this.type = Type.VALUE;
        }

        EnumRailDirection(EnumFacing facing, EnumRailDirection direction, EnumRailDirection fallbackdirection) {
            type = Type.COPYCAT;
            this.direction = new LinkedObjectRotation(facing, direction);
            this.fallbackdirection = fallbackdirection;
        }

        public boolean isAscending() {
            return this == ASCENDING_NORTH || this == ASCENDING_EAST || this == ASCENDING_SOUTH || this == ASCENDING_WEST;
        }

        @Override
        public String getName() {
            return this.name().toLowerCase(Locale.ENGLISH);
        }

        public float getForwardX(EnumFacing face) {
            if(type == Type.COPYCAT) {
                return (direction.getFacing() != face ? direction.getCopied() : fallbackdirection).getForwardX(null);
            }
            return forwardX;
        }

        public float getForwardY(EnumFacing face) {
            if(type == Type.COPYCAT) {
                if(type == Type.COPYCAT) {
                    return (direction.getFacing() != face ? direction.getCopied() : fallbackdirection).getForwardY(null);
                }
            }
            return forwardY;
        }

        public float getForwardZ(EnumFacing face) {
            if(type == Type.COPYCAT) {
                if(type == Type.COPYCAT) {
                    return (direction.getFacing() != face ? direction.getCopied() : fallbackdirection).getForwardZ(null);
                }
            }
            return forwardZ;
        }

        public float getBackwardsX(EnumFacing face) {
            if(type == Type.COPYCAT) {
                if(type == Type.COPYCAT) {
                    return (direction.getFacing() != face ? direction.getCopied() : fallbackdirection).getBackwardsX(null);
                }
            }
            return backwardsX;
        }

        public float getBackwardsY(EnumFacing face) {
            if(type == Type.COPYCAT) {
                if(type == Type.COPYCAT) {
                    return (direction.getFacing() != face ? direction.getCopied() : fallbackdirection).getBackwardsY(null);
                }
            }
            return backwardsY;
        }

        public float getBackwardsZ(EnumFacing face) {
            if(type == Type.COPYCAT) {
                if(type == Type.COPYCAT) {
                    return (direction.getFacing() != face ? direction.getCopied() : fallbackdirection).getBackwardsZ(null);
                }
            }
            return backwardsZ;
        }

        private enum Type {
            VALUE, COPYCAT
        }

        private class LinkedObjectRotation {

            private final EnumFacing facing;
            private final EnumRailDirection copied;

            LinkedObjectRotation(EnumFacing facing, EnumRailDirection copied) {
                this.facing = facing;
                this.copied = copied;
            }

            public EnumFacing getFacing() {
                return facing;
            }

            public EnumRailDirection getCopied() {
                return copied;
            }
        }
    }

    public class Rail
    {
        private final World world;
        private final BlockPos pos;
        private final TourRailBlock block;
        private IBlockState state;
        private final List<BlockPos> connectedRails = Lists.newArrayList();

        public Rail(World worldIn, BlockPos pos, IBlockState state)
        {
            this.world = worldIn;
            this.pos = pos;
            this.state = state;
            this.block = (TourRailBlock)state.getBlock();
            TourRailBlock.EnumRailDirection TourRailBlock$enumraildirection = getRailDirection(worldIn, pos);
            this.updateConnectedRails(TourRailBlock$enumraildirection);
        }

        public List<BlockPos> getConnectedRails()
        {
            return this.connectedRails;
        }

        private void updateConnectedRails(TourRailBlock.EnumRailDirection railDirection)
        {
            this.connectedRails.clear();

            switch (railDirection)
            {
                case NORTH_SOUTH:
                    this.connectedRails.add(this.pos.north());
                    this.connectedRails.add(this.pos.south());
                    break;
                case EAST_WEST:
                    this.connectedRails.add(this.pos.west());
                    this.connectedRails.add(this.pos.east());
                    break;
                case ASCENDING_EAST:
                    this.connectedRails.add(this.pos.west());
                    this.connectedRails.add(this.pos.east().up());
                    break;
                case ASCENDING_WEST:
                    this.connectedRails.add(this.pos.west().up());
                    this.connectedRails.add(this.pos.east());
                    break;
                case ASCENDING_NORTH:
                    this.connectedRails.add(this.pos.north().up());
                    this.connectedRails.add(this.pos.south());
                    break;
                case ASCENDING_SOUTH:
                    this.connectedRails.add(this.pos.north());
                    this.connectedRails.add(this.pos.south().up());
                    break;
                case SOUTH_EAST:
                    this.connectedRails.add(this.pos.east());
                    this.connectedRails.add(this.pos.south());
                    break;
                case SOUTH_WEST:
                    this.connectedRails.add(this.pos.west());
                    this.connectedRails.add(this.pos.south());
                    break;
                case NORTH_WEST:
                    this.connectedRails.add(this.pos.west());
                    this.connectedRails.add(this.pos.north());
                    break;
                case NORTH_EAST:
                    this.connectedRails.add(this.pos.east());
                    this.connectedRails.add(this.pos.north());
                    break;
                case DIAGONAL_NE_SW:
                    this.connectedRails.add(this.pos.north().east());
                    this.connectedRails.add(this.pos.south().west());
                    break;
                case DIAGONAL_NW_SE:
                    this.connectedRails.add(this.pos.north().west());
                    this.connectedRails.add(this.pos.south().east());
                    break;
                case HORIZONTAL_NE:
                    this.connectedRails.add(this.pos.west());
                    this.connectedRails.add(this.pos.north().east());
                    break;
                case HORIZONTAL_SE:
                    this.connectedRails.add(this.pos.west());
                    this.connectedRails.add(this.pos.south().east());
                    break;
                case HORIZONTAL_NW:
                    this.connectedRails.add(this.pos.east());
                    this.connectedRails.add(this.pos.north().west());
                    break;
                case HORIZONTAL_SW:
                    this.connectedRails.add(this.pos.east());
                    this.connectedRails.add(this.pos.south().west());
                    break;
                case VERTICAL_NE:
                    this.connectedRails.add(this.pos.south());
                    this.connectedRails.add(this.pos.north().east());
                    break;
                case VERTICAL_SE:
                    this.connectedRails.add(this.pos.north());
                    this.connectedRails.add(this.pos.south().east());
                    break;
                case VERTICAL_NW:
                    this.connectedRails.add(this.pos.south());
                    this.connectedRails.add(this.pos.north().west());
                    break;
                case VERTICAL_SW:
                    this.connectedRails.add(this.pos.north());
                    this.connectedRails.add(this.pos.south().west());
                    break;
            }
        }

        private void removeSoftConnections()
        {
            for (int i = 0; i < this.connectedRails.size(); ++i)
            {
                TourRailBlock.Rail TourRailBlock$rail = this.findRailAt(this.connectedRails.get(i));

                if (TourRailBlock$rail != null && TourRailBlock$rail.isConnectedToRail(this))
                {
                    this.connectedRails.set(i, TourRailBlock$rail.pos);
                }
                else
                {
                    this.connectedRails.remove(i--);
                }
            }
        }

        private boolean hasRailAt(BlockPos pos)
        {
            return TourRailBlock.isRailBlock(this.world, pos) || TourRailBlock.isRailBlock(this.world, pos.up()) || TourRailBlock.isRailBlock(this.world, pos.down());
        }

        @Nullable
        private TourRailBlock.Rail findRailAt(BlockPos pos)
        {
            IBlockState iblockstate = this.world.getBlockState(pos);

            if (TourRailBlock.isRailBlock(iblockstate))
            {
                return TourRailBlock.this.new Rail(this.world, pos, iblockstate);
            }
            else
            {
                BlockPos lvt_2_1_ = pos.up();
                iblockstate = this.world.getBlockState(lvt_2_1_);

                if (TourRailBlock.isRailBlock(iblockstate))
                {
                    return TourRailBlock.this.new Rail(this.world, lvt_2_1_, iblockstate);
                }
                else
                {
                    lvt_2_1_ = pos.down();
                    iblockstate = this.world.getBlockState(lvt_2_1_);
                    return TourRailBlock.isRailBlock(iblockstate) ? TourRailBlock.this.new Rail(this.world, lvt_2_1_, iblockstate) : null;
                }
            }
        }

        private boolean isConnectedToRail(TourRailBlock.Rail rail)
        {
            return this.isConnectedTo(rail.pos);
        }

        private boolean isConnectedTo(BlockPos posIn)
        {
            for (int i = 0; i < this.connectedRails.size(); ++i)
            {
                BlockPos blockpos = this.connectedRails.get(i);

                if (blockpos.getX() == posIn.getX() && blockpos.getZ() == posIn.getZ())
                {
                    return true;
                }
            }

            return false;
        }

        /**
         * Counts the number of rails adjacent to this rail.
         */
        protected int countAdjacentRails()
        {
            int i = 0;

            for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL)
            {
                if (this.hasRailAt(this.pos.offset(enumfacing)))
                {
                    ++i;
                }
            }

            for(int i1 = 0; i1 < 4; i1++) {
                if(this.hasRailAt(this.pos.north(i1 % 2 == 0 ? 1 : -1).east(Math.floorDiv(i1, 2) == 0 ? 1 : -1))) {
                    ++i;
                }
            }

            return i;
        }

        private boolean canConnectTo(TourRailBlock.Rail rail)
        {
            return this.isConnectedToRail(rail) || this.connectedRails.size() != 2;
        }

        private void connectTo(TourRailBlock.Rail rail)
        {
            this.connectedRails.add(rail.pos);
            BlockPos blockpos = this.pos.north();
            BlockPos blockpos1 = this.pos.south();
            BlockPos blockpos2 = this.pos.west();
            BlockPos blockpos3 = this.pos.east();
            boolean flag = this.isConnectedTo(blockpos);
            boolean flag1 = this.isConnectedTo(blockpos1);
            boolean flag2 = this.isConnectedTo(blockpos2);
            boolean flag3 = this.isConnectedTo(blockpos3);

            TourRailBlock.EnumRailDirection railDirection = null;

            if (flag || flag1)
            {
                railDirection = TourRailBlock.EnumRailDirection.NORTH_SOUTH;
            }

            if (flag2 || flag3)
            {
                railDirection = TourRailBlock.EnumRailDirection.EAST_WEST;
            }

            if (flag1 && flag3 && !flag && !flag2)
            {
                railDirection = TourRailBlock.EnumRailDirection.SOUTH_EAST;
            }

            if (flag1 && flag2 && !flag && !flag3)
            {
                railDirection = TourRailBlock.EnumRailDirection.SOUTH_WEST;
            }

            if (flag && flag2 && !flag1 && !flag3)
            {
                railDirection = TourRailBlock.EnumRailDirection.NORTH_WEST;
            }

            if (flag && flag3 && !flag1 && !flag2)
            {
                railDirection = TourRailBlock.EnumRailDirection.NORTH_EAST;
            }

            if (railDirection == TourRailBlock.EnumRailDirection.NORTH_SOUTH)
            {
                if (TourRailBlock.isRailBlock(this.world, blockpos.up()))
                {
                    railDirection = TourRailBlock.EnumRailDirection.ASCENDING_NORTH;
                }

                if (TourRailBlock.isRailBlock(this.world, blockpos1.up()))
                {
                    railDirection = TourRailBlock.EnumRailDirection.ASCENDING_SOUTH;
                }
            }

            if (railDirection == TourRailBlock.EnumRailDirection.EAST_WEST)
            {
                if (TourRailBlock.isRailBlock(this.world, blockpos3.up()))
                {
                    railDirection = TourRailBlock.EnumRailDirection.ASCENDING_EAST;
                }

                if (TourRailBlock.isRailBlock(this.world, blockpos2.up()))
                {
                    railDirection = TourRailBlock.EnumRailDirection.ASCENDING_WEST;
                }
            }

            if (railDirection == null) {
                BlockPos nw = this.pos.north().west();
                BlockPos ne = this.pos.north().east();
                BlockPos sw = this.pos.south().west();
                BlockPos se = this.pos.south().east();

                boolean can_nw = this.isConnectedTo(nw);
                boolean can_ne = this.isConnectedTo(ne);
                boolean can_sw = this.isConnectedTo(sw);
                boolean can_se = this.isConnectedTo(se);
                if(can_ne || can_sw) {
                    railDirection = EnumRailDirection.DIAGONAL_NE_SW;
                } else if(can_nw || can_se) {
                    railDirection = EnumRailDirection.DIAGONAL_NW_SE;
                } else {
                    railDirection = TourRailBlock.EnumRailDirection.NORTH_SOUTH;
                }
            }

            boolean connectDiag = false;
            StringBuilder connectionName = new StringBuilder();

            if(railDirection == EnumRailDirection.NORTH_SOUTH) {
                connectionName.append("VERTICAL_");
                boolean connectedNorth = this.isConnectedTo(pos.north());
                boolean connectedSouth = this.isConnectedTo(pos.south());
                if(!connectedNorth || !connectedSouth) {
                    connectionName.append(connectedNorth ? "S" : "N");
                    BlockPos connectPos = pos.north(connectedNorth ? -1 : 1);
                    if(this.isConnectedTo(connectPos.east())) {
                        connectionName.append("E");
                        connectDiag = true;
                    } else if(this.isConnectedTo(connectPos.west())) {
                        connectionName.append("W");
                        connectDiag = true;
                    }

                }
            } else if(railDirection == EnumRailDirection.EAST_WEST) {
                connectionName.append("HORIZONTAL_");
                boolean connectedEast = this.isConnectedTo(pos.east());
                boolean connectedWest = this.isConnectedTo(pos.west());
                if(!connectedEast || !connectedWest) {
                    String suffix = connectedEast ? "W" : "E";
                    BlockPos connectPos = pos.east(connectedEast ? -1 : 1);
                    if(this.isConnectedTo(connectPos.north())) {
                        connectionName.append("N");
                        connectDiag = true;
                    } else if(this.isConnectedTo(connectPos.south())) {
                        connectionName.append("S");
                        connectDiag = true;
                    }
                    connectionName.append(suffix);
                }
            }

            if(connectDiag) {
                railDirection = EnumRailDirection.valueOf(connectionName.toString());
            }


            this.world.setBlockState(this.pos, this.state, 3);
            ((TourRailBlockEntity)world.getTileEntity(pos)).setDirection(railDirection);
        }

        private boolean hasNeighborRail(BlockPos posIn)
        {
            TourRailBlock.Rail TourRailBlock$rail = this.findRailAt(posIn);

            if (TourRailBlock$rail == null)
            {
                return false;
            }
            else
            {
                TourRailBlock$rail.removeSoftConnections();
                return TourRailBlock$rail.canConnectTo(this);
            }
        }

        public TourRailBlock.Rail place(boolean powered, boolean initialPlacement)
        {
            BlockPos blockpos = this.pos.north();
            BlockPos blockpos1 = this.pos.south();
            BlockPos blockpos2 = this.pos.west();
            BlockPos blockpos3 = this.pos.east();
            boolean flag = this.hasNeighborRail(blockpos);
            boolean flag1 = this.hasNeighborRail(blockpos1);
            boolean flag2 = this.hasNeighborRail(blockpos2);
            boolean flag3 = this.hasNeighborRail(blockpos3);
            TourRailBlock.EnumRailDirection railDirection = null;

            if ((flag || flag1) && !flag2 && !flag3)
            {
                railDirection = TourRailBlock.EnumRailDirection.NORTH_SOUTH;
            }

            if ((flag2 || flag3) && !flag && !flag1)
            {
                railDirection = TourRailBlock.EnumRailDirection.EAST_WEST;
            }

            if (flag1 && flag3 && !flag && !flag2)
            {
                railDirection = TourRailBlock.EnumRailDirection.SOUTH_EAST;
            }

            if (flag1 && flag2 && !flag && !flag3)
            {
                railDirection = TourRailBlock.EnumRailDirection.SOUTH_WEST;
            }

            if (flag && flag2 && !flag1 && !flag3)
            {
                railDirection = TourRailBlock.EnumRailDirection.NORTH_WEST;
            }

            if (flag && flag3 && !flag1 && !flag2)
            {
                railDirection = TourRailBlock.EnumRailDirection.NORTH_EAST;
            }

            if (railDirection == null)
            {
                if (flag || flag1)
                {
                    railDirection = TourRailBlock.EnumRailDirection.NORTH_SOUTH;
                }

                if (flag2 || flag3)
                {
                    railDirection = TourRailBlock.EnumRailDirection.EAST_WEST;
                }
            }

            if (railDirection == TourRailBlock.EnumRailDirection.NORTH_SOUTH)
            {
                if (TourRailBlock.isRailBlock(this.world, blockpos.up()))
                {
                    railDirection = TourRailBlock.EnumRailDirection.ASCENDING_NORTH;
                }

                if (TourRailBlock.isRailBlock(this.world, blockpos1.up()))
                {
                    railDirection = TourRailBlock.EnumRailDirection.ASCENDING_SOUTH;
                }
            }

            if (railDirection == TourRailBlock.EnumRailDirection.EAST_WEST)
            {
                if (TourRailBlock.isRailBlock(this.world, blockpos3.up()))
                {
                    railDirection = TourRailBlock.EnumRailDirection.ASCENDING_EAST;
                }

                if (TourRailBlock.isRailBlock(this.world, blockpos2.up()))
                {
                    railDirection = TourRailBlock.EnumRailDirection.ASCENDING_WEST;
                }
            }

            if (railDirection == null) {
                BlockPos nw = this.pos.north().west();
                BlockPos ne = this.pos.north().east();
                BlockPos sw = this.pos.south().west();
                BlockPos se = this.pos.south().east();

                boolean can_nw = this.hasNeighborRail(nw);
                boolean can_ne = this.hasNeighborRail(ne);
                boolean can_sw = this.hasNeighborRail(sw);
                boolean can_se = this.hasNeighborRail(se);
                if(can_ne || can_sw) {
                    railDirection = EnumRailDirection.DIAGONAL_NE_SW;
                } else if(can_nw || can_se) {
                    railDirection = EnumRailDirection.DIAGONAL_NW_SE;
                } else {
                    railDirection = TourRailBlock.EnumRailDirection.NORTH_SOUTH;
                }
            }

            boolean connectDiag = false;
            StringBuilder connectionName = new StringBuilder();

            if(railDirection == EnumRailDirection.NORTH_SOUTH) {
                connectionName.append("VERTICAL_");
                boolean connectedNorth = this.hasNeighborRail(pos.north());
                boolean connectedSouth = this.hasNeighborRail(pos.south());
                if(!connectedNorth || !connectedSouth) {
                    connectionName.append(connectedNorth ? "S" : "N");
                    BlockPos connectPos = pos.north(connectedNorth ? -1 : 1);
                    if(this.hasNeighborRail(connectPos.east())) {
                        connectionName.append("E");
                        connectDiag = true;
                    } else if(this.hasNeighborRail(connectPos.west())) {
                        connectionName.append("W");
                        connectDiag = true;
                    }

                }
            } else if(railDirection == EnumRailDirection.EAST_WEST) {
                connectionName.append("HORIZONTAL_");
                boolean connectedEast = this.hasNeighborRail(pos.east());
                boolean connectedWest = this.hasNeighborRail(pos.west());
                if(!connectedEast || !connectedWest) {
                    String suffix = connectedEast ? "W" : "E";
                    BlockPos connectPos = pos.east(connectedEast ? -1 : 1);
                    if(this.hasNeighborRail(connectPos.north())) {
                        connectionName.append("N");
                        connectDiag = true;
                    } else if(this.hasNeighborRail(connectPos.south())) {
                        connectionName.append("S");
                        connectDiag = true;
                    }
                    connectionName.append(suffix);
                }
            }

            if(connectDiag) {
                railDirection = EnumRailDirection.valueOf(connectionName.toString());
            }

            this.updateConnectedRails(railDirection);

            if (initialPlacement || ((TourRailBlockEntity)world.getTileEntity(pos)).getDirection() != railDirection)
            {
                this.world.setBlockState(this.pos, this.state, 3);
                ((TourRailBlockEntity)world.getTileEntity(pos)).setDirection(railDirection);

                for (int i = 0; i < this.connectedRails.size(); ++i)
                {
                    TourRailBlock.Rail TourRailBlock$rail = this.findRailAt(this.connectedRails.get(i));

                    if (TourRailBlock$rail != null)
                    {
                        TourRailBlock$rail.removeSoftConnections();

                        if (TourRailBlock$rail.canConnectTo(this))
                        {
                            TourRailBlock$rail.connectTo(this);
                        }
                    }
                }
            }

            return this;
        }

        public IBlockState getBlockState()
        {
            return this.state;
        }
    }
}
