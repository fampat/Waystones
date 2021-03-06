package net.blay09.mods.waystones.item;

import net.blay09.mods.waystones.PlayerWaystoneHelper;
import net.blay09.mods.waystones.WaystoneManager;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemReturnScroll extends Item {

	public static final String name = "return_scroll";
	public static final ResourceLocation registryName = new ResourceLocation(Waystones.MOD_ID, name);

	public ItemReturnScroll() {
		setCreativeTab(Waystones.creativeTab);
		setRegistryName(name);
		setUnlocalizedName(registryName.toString());
	}

	@Override
	public int getMaxItemUseDuration(ItemStack itemStack) {
		return 32;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack itemStack) {
		return EnumAction.BOW;
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack itemStack, World world, EntityLivingBase entity) {
		if(!world.isRemote && entity instanceof EntityPlayer) {
			WaystoneEntry lastEntry = PlayerWaystoneHelper.getLastWaystone((EntityPlayer) entity);
			if(lastEntry != null) {
				if(WaystoneManager.teleportToWaystone((EntityPlayer) entity, lastEntry)) {
					if(!((EntityPlayer) entity).capabilities.isCreativeMode) {
						itemStack.shrink(1);
					}
				}
			}
		}
		return itemStack;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack itemStack = player.getHeldItem(hand);
		if(PlayerWaystoneHelper.getLastWaystone(player) != null) {
			if(!player.isHandActive() && world.isRemote) {
				Waystones.proxy.playSound(SoundEvents.BLOCK_PORTAL_TRIGGER, new BlockPos(player.posX, player.posY, player.posZ), 2f);
			}
			player.setActiveHand(hand);
			return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
		} else {
			player.sendStatusMessage(new TextComponentTranslation("waystones:scrollNotBound"), true);
			return new ActionResult<>(EnumActionResult.FAIL, itemStack);
		}

	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
		EntityPlayer player = Minecraft.getMinecraft().player;
		if(player == null) {
			return;
		}
		WaystoneEntry lastEntry = PlayerWaystoneHelper.getLastWaystone(player);
		if(lastEntry != null) {
			tooltip.add(TextFormatting.GRAY + I18n.format("tooltip.waystones:boundTo", TextFormatting.DARK_AQUA + lastEntry.getName()));
		} else {
			tooltip.add(TextFormatting.GRAY + I18n.format("tooltip.waystones:boundTo", I18n.format("tooltip.waystones:none")));
		}
	}

}
