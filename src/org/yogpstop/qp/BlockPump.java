package org.yogpstop.qp;

import static buildcraft.core.CreativeTabBuildCraft.tabBuildCraft;

import java.util.ArrayList;

import buildcraft.api.tools.IToolWrench;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet3Chat;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class BlockPump extends BlockContainer {

	public BlockPump(int i) {
		super(i, Material.iron);
		setHardness(5F);
		setCreativeTab(tabBuildCraft);
		setBlockName("PumpPlus");
	}

	@Override
	public TileEntity createNewTileEntity(World var1) {
		return new TilePump();
	}

	@Override
	public int getBlockTextureFromSideAndMetadata(int i, int j) {
		switch (i) {
		case 0:
			return 65;
		case 1:
			return 66;
		default:
			return 64;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getBlockTexture(IBlockAccess ba, int x, int y, int z, int side) {
		TileEntity tile = ba.getBlockTileEntity(x, y, z);
		if (tile instanceof TilePump && side == 1) {
			if (((TilePump) tile).G_working()) return 68;
			if (((TilePump) tile).G_connected() != null) return 67;
		}
		return super.getBlockTexture(ba, x, y, z, side);
	}

	@Override
	public String getTextureFile() {
		return "/mods/yogpstop_qp/textures/textures.png";
	}

	private final ArrayList<ItemStack> drop = new ArrayList<ItemStack>();

	@Override
	public void breakBlock(World world, int x, int y, int z, int id, int meta) {
		this.drop.clear();
		TilePump tp = (TilePump) world.getBlockTileEntity(x, y, z);
		if (world.isRemote || tp == null) return;
		int count = quantityDropped(meta, 0, world.rand);
		int id1 = idDropped(meta, world.rand, 0);
		if (id1 > 0) {
			for (int i = 0; i < count; i++) {
				ItemStack is = new ItemStack(id1, 1, damageDropped(meta));
				tp.S_setEnchantment(is);
				this.drop.add(is);
			}
		}
		super.breakBlock(world, x, y, z, id, meta);
	}

	@Override
	public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata, int fortune) {
		return this.drop;
	}

	@Override
	public void onBlockPlacedBy(World w, int x, int y, int z, EntityLiving el) {
		super.onBlockPlacedBy(w, x, y, z, el);
		((TilePump) w.getBlockTileEntity(x, y, z)).G_init(el.getHeldItem().getEnchantmentTagList());
	}

	@Override
	public void onNeighborBlockChange(World w, int x, int y, int z, int bid) {
		((TilePump) w.getBlockTileEntity(x, y, z)).G_reinit();
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int side, float par7, float par8, float par9) {
		Item equipped = ep.getCurrentEquippedItem() != null ? ep.getCurrentEquippedItem().getItem() : null;
		if (equipped instanceof IToolWrench && ((IToolWrench) equipped).canWrench(ep, x, y, z)) {
			((IToolWrench) equipped).wrenchUsed(ep, x, y, z);
			if (world.isRemote) return true;
			((TilePump) world.getBlockTileEntity(x, y, z)).S_changeRange(ep);
			return true;
		}
		if (equipped instanceof ItemTool) {
			if (ep.getCurrentEquippedItem().getItemDamage() == 0) {
				if (world.isRemote) return true;
				PacketDispatcher.sendPacketToPlayer(new Packet3Chat(StatCollector.translateToLocal("chat.pumplist")), (Player) ep);
				for (String s : ((TilePump) world.getBlockTileEntity(x, y, z)).C_getNames())
					PacketDispatcher.sendPacketToPlayer(new Packet3Chat(s), (Player) ep);
				PacketDispatcher.sendPacketToPlayer(new Packet3Chat(StatCollector.translateToLocal("chat.plusenchant")), (Player) ep);
				for (String s : ((TilePump) world.getBlockTileEntity(x, y, z)).C_getEnchantments())
					PacketDispatcher.sendPacketToPlayer(new Packet3Chat(s), (Player) ep);
				return true;
			}
			if (ep.getCurrentEquippedItem().getItemDamage() == 2) {
				if (world.isRemote) return true;
				PacketDispatcher.sendPacketToPlayer(
						new Packet3Chat(StatCollector.translateToLocalFormatted("chat.pumptoggle", ((TilePump) world.getBlockTileEntity(x, y, z)).incl(side),
								TilePump.fdToString(ForgeDirection.getOrientation(side)))), (Player) ep);
				return true;
			}
		}
		return false;
	}
}
