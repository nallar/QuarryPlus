package org.yogpstop.qp;

import static buildcraft.core.CreativeTabBuildCraft.tabBuildCraft;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class BlockMover extends Block {

	public BlockMover(int par1) {
		super(par1, Material.iron);
		setHardness(1.2F);
		this.setCreativeTab(tabBuildCraft);
		setBlockName("EnchantMover");
	}

	@Override
	public int getBlockTextureFromSideAndMetadata(int i, int j) {

		switch (i) {
		case 1:
			return 34;
		case 0:
			return 33;
		default:
			return 32;
		}
	}

	@Override
	public String getTextureFile() {
		return "/mods/yogpstop_qp/textures/textures.png";
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int par6, float par7, float par8, float par9) {
		if (world.isRemote) return true;
		ep.openGui(QuarryPlus.instance, QuarryPlus.guiIdMover, world, x, y, z);
		return true;
	}
}
