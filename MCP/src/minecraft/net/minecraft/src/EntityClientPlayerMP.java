package net.minecraft.src;

import net.minecraft.client.Minecraft;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityPlayerSP;
import net.minecraft.src.ItemStack;
import net.minecraft.src.MathHelper;
import net.minecraft.src.NetClientHandler;
import net.minecraft.src.Packet101CloseWindow;
import net.minecraft.src.Packet10Flying;
import net.minecraft.src.Packet11PlayerPosition;
import net.minecraft.src.Packet12PlayerLook;
import net.minecraft.src.Packet13PlayerLookMove;
import net.minecraft.src.Packet14BlockDig;
import net.minecraft.src.Packet18Animation;
import net.minecraft.src.Packet19EntityAction;
import net.minecraft.src.Packet3Chat;
import net.minecraft.src.Packet9Respawn;
import net.minecraft.src.Session;
import net.minecraft.src.StatBase;
import net.minecraft.src.World;

import org.bukkitcontrib.gui.ScreenType;
import org.bukkitcontrib.packet.*; //BukkitContrib
import org.bukkitcontrib.player.*; //BukkitContrib

public class EntityClientPlayerMP extends EntityPlayerSP {

	public NetClientHandler sendQueue;
	private int field_9380_bx = 0;
	private boolean field_21093_bH = false;
	private double oldPosX;
	private double field_9378_bz;
	private double oldPosY;
	private double oldPosZ;
	private float oldRotationYaw;
	private float oldRotationPitch;
	private boolean field_9382_bF = false;
	private boolean wasSneaking = false;
	private int field_12242_bI = 0;
	//BukkitContrib Start
	private KeyBinding fogKey = null;
	//BukkitContrib End


	public EntityClientPlayerMP(Minecraft var1, World var2, Session var3, NetClientHandler var4) {
		super(var1, var2, var3, 0);
		this.sendQueue = var4;
	}

	public boolean attackEntityFrom(Entity var1, int var2) {
		return false;
	}

	public void heal(int var1) {}

	public void onUpdate() {
		if(this.worldObj.blockExists(MathHelper.floor_double(this.posX), 64, MathHelper.floor_double(this.posZ))) {
			super.onUpdate();
			this.func_4056_N();
		}
		//BukkitContrib Start
		if (fogKey != null) {
			BukkitContrib.getGameInstance().gameSettings.keyBindToggleFog = fogKey;
			fogKey = null;
		}
		//BukkitContrib End
	}

	public void func_4056_N() {
		if(this.field_9380_bx++ == 20) {
			this.sendInventoryChanged();
			this.field_9380_bx = 0;
		}

		boolean var1 = this.isSneaking();
		if(var1 != this.wasSneaking) {
			if(var1) {
				this.sendQueue.addToSendQueue(new Packet19EntityAction(this, 1));
			} else {
				this.sendQueue.addToSendQueue(new Packet19EntityAction(this, 2));
			}

			this.wasSneaking = var1;
		}

		double var2 = this.posX - this.oldPosX;
		double var4 = this.boundingBox.minY - this.field_9378_bz;
		double var6 = this.posY - this.oldPosY;
		double var8 = this.posZ - this.oldPosZ;
		double var10 = (double)(this.rotationYaw - this.oldRotationYaw);
		double var12 = (double)(this.rotationPitch - this.oldRotationPitch);
		boolean var14 = var4 != 0.0D || var6 != 0.0D || var2 != 0.0D || var8 != 0.0D;
		boolean var15 = var10 != 0.0D || var12 != 0.0D;
		if(this.ridingEntity != null) {
			if(var15) {
				this.sendQueue.addToSendQueue(new Packet11PlayerPosition(this.motionX, -999.0D, -999.0D, this.motionZ, this.onGround));
			} else {
				this.sendQueue.addToSendQueue(new Packet13PlayerLookMove(this.motionX, -999.0D, -999.0D, this.motionZ, this.rotationYaw, this.rotationPitch, this.onGround));
			}

			var14 = false;
		} else if(var14 && var15) {
			this.sendQueue.addToSendQueue(new Packet13PlayerLookMove(this.posX, this.boundingBox.minY, this.posY, this.posZ, this.rotationYaw, this.rotationPitch, this.onGround));
			this.field_12242_bI = 0;
		} else if(var14) {
			this.sendQueue.addToSendQueue(new Packet11PlayerPosition(this.posX, this.boundingBox.minY, this.posY, this.posZ, this.onGround));
			this.field_12242_bI = 0;
		} else if(var15) {
			this.sendQueue.addToSendQueue(new Packet12PlayerLook(this.rotationYaw, this.rotationPitch, this.onGround));
			this.field_12242_bI = 0;
		} else {
			this.sendQueue.addToSendQueue(new Packet10Flying(this.onGround));
			if(this.field_9382_bF == this.onGround && this.field_12242_bI <= 200) {
				++this.field_12242_bI;
			} else {
				this.field_12242_bI = 0;
			}
		}

		this.field_9382_bF = this.onGround;
		if(var14) {
			this.oldPosX = this.posX;
			this.field_9378_bz = this.boundingBox.minY;
			this.oldPosY = this.posY;
			this.oldPosZ = this.posZ;
		}

		if(var15) {
			this.oldRotationYaw = this.rotationYaw;
			this.oldRotationPitch = this.rotationPitch;
		}

	}

	public void dropCurrentItem() {
		this.sendQueue.addToSendQueue(new Packet14BlockDig(4, 0, 0, 0, 0));
	}

	private void sendInventoryChanged() {}

	protected void joinEntityItemWithWorld(EntityItem var1) {}

	public void sendChatMessage(String var1) {
		this.sendQueue.addToSendQueue(new Packet3Chat(var1));
	}

	public void swingItem() {
		super.swingItem();
		this.sendQueue.addToSendQueue(new Packet18Animation(this, 1));
	}

	public void respawnPlayer() {
		this.sendInventoryChanged();
		this.sendQueue.addToSendQueue(new Packet9Respawn((byte)this.dimension));
	}

	protected void damageEntity(int var1) {
		this.health -= var1;
	}

	public void closeScreen() {
		this.sendQueue.addToSendQueue(new Packet101CloseWindow(this.craftingInventory.windowId));
		this.inventory.setItemStack((ItemStack)null);
		super.closeScreen();
	}

	public void setHealth(int var1) {
		if(this.field_21093_bH) {
			super.setHealth(var1);
		} else {
			this.health = var1;
			this.field_21093_bH = true;
		}

	}

	public void addStat(StatBase var1, int var2) {
		if(var1 != null) {
			if(var1.isIndependent) {
				super.addStat(var1, var2);
			}

		}
	}

	public void func_27027_b(StatBase var1, int var2) {
		if(var1 != null) {
			if(!var1.isIndependent) {
				super.addStat(var1, var2);
			}

		}
	}
	//BukkitContrib Start
	public void handleKeyPress(int i, boolean keyReleased) {
		//key bindings
		if (keyReleased) {
			String keyName = org.lwjgl.input.Keyboard.getKeyName(i);
			if (keyName != null && keyName.length() == 1) {
				char ch = keyName.charAt(0);
				if (BukkitContrib.getChatManager().getBoundCommand(ch) != null) {
					BukkitContrib.getChatManager().sendChat(BukkitContrib.getChatManager().getBoundCommand(ch));
				}
			}
		}
		if (BukkitContrib.isEnabled()) {
				sendQueue.addToSendQueue(new CustomPacket(new PacketKeyPress((byte)i, keyReleased, (MovementInputFromOptions)movementInput, ScreenType.GAME_SCREEN)));
				if (BukkitContrib.getVersion() > 5 && keyReleased) {
					final GameSettings settings = BukkitContrib.getGameInstance().gameSettings;
					if (i == settings.keyBindToggleFog.keyCode) {
						byte view = (byte)settings.renderDistance;
						byte newView = BukkitContrib.getNextRenderDistance(view);
						fogKey = settings.keyBindToggleFog;
						settings.keyBindToggleFog = new KeyBinding("key.fog", -1);
						if (view != newView) {
							settings.renderDistance = newView;
							sendQueue.addToSendQueue(new CustomPacket(new PacketRenderDistance((byte)newView)));
						}
					}
				}
		}
		
		super.handleKeyPress(i, keyReleased);
	}
	//BukkitContrib End
	
	//BukkitContrib Start
	public void updateCloak() {
		if (this.cloakUrl == null || this.playerCloakUrl == null) {
				super.updateCloak();
				System.out.println("UpdateCloak");
		}
	}
	//BukkitContrib End
}
