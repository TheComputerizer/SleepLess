package mods.thecomputerizer.sleepless.registry.blocks;

import mods.thecomputerizer.sleepless.registry.tiles.TestBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static net.minecraft.block.material.MapColor.ICE;
import static net.minecraft.block.material.Material.ANVIL;

public class TestBlock extends Block implements ITileEntityProvider {

    public TestBlock() {
        super(ANVIL,ICE);
    }
    
    @Override public @Nullable TileEntity createNewTileEntity(@Nonnull World world, int meta) {
        return new TestBlockEntity();
    }
}
