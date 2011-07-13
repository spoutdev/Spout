package net.minecraft.src;

import net.minecraft.src.Entity;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.MathHelper;
import net.minecraft.src.World;

public class EntityOtherPlayerMP extends EntityPlayer {

	private int field_785_bg;
	private double field_784_bh;
	private double field_783_bi;
	private double field_782_bj;
	private double field_780_bk;
	private double field_786_bl;
	float field_20924_a = 0.0F;


	public EntityOtherPlayerMP(World var1, String var2) {
		super(var1);
		this.username = var2;
		this.yOffset = 0.0F;
		this.stepHeight = 0.0F;
		if(var2 != null && var2.length() > 0) {
			this.skinUrl = "http://s3.amazonaws.com/MinecraftSkins/" + var2 + ".png";
		}

		this.noClip = true;
		this.field_22062_y = 0.25F;
		this.renderDistanceWeight = 10.0D;
	}

	protected void resetHeight() {
		this.yOffset = 0.0F;
	}

	public boolean attackEntityFrom(Entity var1, int var2) {
		return true;
	}

	public void setPositionAndRotation2(double var1, double var3, double var5, float var7, float var8, int var9) {
		this.field_784_bh = var1;
		this.field_783_bi = var3;
		this.field_782_bj = var5;
		this.field_780_bk = (double)var7;
		this.field_786_bl = (double)var8;
		this.field_785_bg = var9;
	}

	public void onUpdate() {
		this.field_22062_y = 0.0F;
		super.onUpdate();
		this.field_705_Q = this.field_704_R;
		double var1 = this.posX - this.prevPosX;
		double var3 = this.posZ - this.prevPosZ;
		float var5 = MathHelper.sqrt_double(var1 * var1 + var3 * var3) * 4.0F;
		if(var5 > 1.0F) {
			var5 = 1.0F;
		}

		this.field_704_R += (var5 - this.field_704_R) * 0.4F;
		this.field_703_S += this.field_704_R;
	}

	public float getShadowSize() {
		return 0.0F;
	}

	public void onLivingUpdate() {
		super.updatePlayerActionState();
		if(this.field_785_bg > 0) {
			double var1 = this.posX + (this.field_784_bh - this.posX) / (double)this.field_785_bg;
			double var3 = this.posY + (this.field_783_bi - this.posY) / (double)this.field_785_bg;
			double var5 = this.posZ + (this.field_782_bj - this.posZ) / (double)this.field_785_bg;

			double var7;
			for(var7 = this.field_780_bk - (double)this.rotationYaw; var7 < -180.0D; var7 += 360.0D) {
				;
			}

			while(var7 >= 180.0D) {
				var7 -= 360.0D;
			}

			this.rotationYaw = (float)((double)this.rotationYaw + var7 / (double)this.field_785_bg);
			this.rotationPitch = (float)((double)this.rotationPitch + (this.field_786_bl - (double)this.rotationPitch) / (double)this.field_785_bg);
			--this.field_785_bg;
			this.setPosition(var1, var3, var5);
			this.setRotation(this.rotationYaw, this.rotationPitch);
		}

		this.field_775_e = this.field_774_f;
		float var9 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
		float var2 = (float)Math.atan(-this.motionY * 0.20000000298023224D) * 15.0F;
		if(var9 > 0.1F) {
			var9 = 0.1F;
		}

		if(!this.onGround || this.health <= 0) {
			var9 = 0.0F;
		}

		if(this.onGround || this.health <= 0) {
			var2 = 0.0F;
		}

		this.field_774_f += (var9 - this.field_774_f) * 0.4F;
		this.field_9328_R += (var2 - this.field_9328_R) * 0.8F;
	}

	public void outfitWithItem(int var1, int var2, int var3) {
		ItemStack var4 = null;
		if(var2 >= 0) {
			var4 = new ItemStack(var2, 1, var3);
		}

		if(var1 == 0) {
			this.inventory.mainInventory[this.inventory.currentItem] = var4;
		} else {
			this.inventory.armorInventory[var1 - 1] = var4;
		}

	}

	public void func_6420_o() {}
	
	 //BukkitContrib Start
	 public void updateCloak() {
		  if (this.cloakUrl == null || this.playerCloakUrl == null) {
				super.updateCloak();
		  }
	 }
	 //BukkitContrib End
}
