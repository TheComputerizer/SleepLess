package mods.thecomputerizer.sleepless.registry.blocks;

import mods.thecomputerizer.sleepless.registry.tiles.TestBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TestBlock extends Block implements ITileEntityProvider {

    public TestBlock() {
        super(Material.ANVIL,MapColor.ICE);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(@Nonnull World world, int meta) {
        return new TestBlockEntity();
    }
}
