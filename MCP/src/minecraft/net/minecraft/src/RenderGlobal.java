package net.minecraft.src;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityBubbleFX;
import net.minecraft.src.EntityExplodeFX;
import net.minecraft.src.EntityFlameFX;
import net.minecraft.src.EntityFootStepFX;
import net.minecraft.src.EntityHeartFX;
import net.minecraft.src.EntityLavaFX;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityNoteFX;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPortalFX;
import net.minecraft.src.EntityReddustFX;
import net.minecraft.src.EntityRenderer;
import net.minecraft.src.EntitySlimeFX;
import net.minecraft.src.EntitySmokeFX;
import net.minecraft.src.EntitySnowShovelFX;
import net.minecraft.src.EntitySorter;
import net.minecraft.src.EntitySplashFX;
import net.minecraft.src.EnumMovingObjectType;
import net.minecraft.src.GLAllocation;
import net.minecraft.src.ICamera;
import net.minecraft.src.IWorldAccess;
import net.minecraft.src.ImageBufferDownload;
import net.minecraft.src.Item;
import net.minecraft.src.ItemRecord;
import net.minecraft.src.ItemStack;
import net.minecraft.src.MathHelper;
import net.minecraft.src.MovingObjectPosition;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.RenderEngine;
import net.minecraft.src.RenderHelper;
import net.minecraft.src.RenderList;
import net.minecraft.src.RenderManager;
import net.minecraft.src.RenderSorter;
import net.minecraft.src.Tessellator;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntityRenderer;
import net.minecraft.src.Vec3D;
import net.minecraft.src.World;
import net.minecraft.src.WorldRenderer;
import org.lwjgl.opengl.ARBOcclusionQuery;
import org.lwjgl.opengl.GL11;
//BukkitContrib Start
import org.getspout.io.CustomTextureManager;
//BukkitContrib End

public class RenderGlobal implements IWorldAccess {

	public List tileEntities = new ArrayList();
	private World worldObj;
	private RenderEngine renderEngine;
	private List worldRenderersToUpdate = new ArrayList();
	private WorldRenderer[] sortedWorldRenderers;
	private WorldRenderer[] worldRenderers;
	private int renderChunksWide;
	private int renderChunksTall;
	private int renderChunksDeep;
	private int glRenderListBase;
	private Minecraft mc;
	private RenderBlocks globalRenderBlocks;
	private IntBuffer glOcclusionQueryBase;
	private boolean occlusionEnabled = false;
	private int cloudOffsetX = 0;
	private int starGLCallList;
	private int glSkyList;
	private int glSkyList2;
	private int minBlockX;
	private int minBlockY;
	private int minBlockZ;
	private int maxBlockX;
	private int maxBlockY;
	private int maxBlockZ;
	private int renderDistance = -1;
	private int renderEntitiesStartupCounter = 2;
	private int countEntitiesTotal;
	private int countEntitiesRendered;
	private int countEntitiesHidden;
	int[] dummyBuf50k = new int['\uc350'];
	IntBuffer occlusionResult = GLAllocation.createDirectIntBuffer(64);
	private int renderersLoaded;
	private int renderersBeingClipped;
	private int renderersBeingOccluded;
	private int renderersBeingRendered;
	private int renderersSkippingRenderPass;
	private int worldRenderersCheckIndex;
	private List glRenderLists = new ArrayList();
	private RenderList[] allRenderLists = new RenderList[]{new RenderList(), new RenderList(), new RenderList(), new RenderList()};
	int dummyInt0 = 0;
	int glDummyList = GLAllocation.generateDisplayLists(1);
	double prevSortX = -9999.0D;
	double prevSortY = -9999.0D;
	double prevSortZ = -9999.0D;
	public float damagePartialTime;
	int frustrumCheckOffset = 0;


	public RenderGlobal(Minecraft var1, RenderEngine var2) {
		this.mc = var1;
		this.renderEngine = var2;
		byte var3 = 64;
		this.glRenderListBase = GLAllocation.generateDisplayLists(var3 * var3 * var3 * 3);
		this.occlusionEnabled = var1.getOpenGlCapsChecker().checkARBOcclusion();
		if(this.occlusionEnabled) {
			this.occlusionResult.clear();
			this.glOcclusionQueryBase = GLAllocation.createDirectIntBuffer(var3 * var3 * var3);
			this.glOcclusionQueryBase.clear();
			this.glOcclusionQueryBase.position(0);
			this.glOcclusionQueryBase.limit(var3 * var3 * var3);
			ARBOcclusionQuery.glGenQueriesARB(this.glOcclusionQueryBase);
		}

		this.starGLCallList = GLAllocation.generateDisplayLists(3);
		GL11.glPushMatrix();
		GL11.glNewList(this.starGLCallList, 4864 /*GL_COMPILE*/);
		this.renderStars();
		GL11.glEndList();
		GL11.glPopMatrix();
		Tessellator var4 = Tessellator.instance;
		this.glSkyList = this.starGLCallList + 1;
		GL11.glNewList(this.glSkyList, 4864 /*GL_COMPILE*/);
		byte var6 = 64;
		int var7 = 256 / var6 + 2;
		float var5 = 16.0F;

		int var8;
		int var9;
		for(var8 = -var6 * var7; var8 <= var6 * var7; var8 += var6) {
			for(var9 = -var6 * var7; var9 <= var6 * var7; var9 += var6) {
				var4.startDrawingQuads();
				var4.addVertex((double)(var8 + 0), (double)var5, (double)(var9 + 0));
				var4.addVertex((double)(var8 + var6), (double)var5, (double)(var9 + 0));
				var4.addVertex((double)(var8 + var6), (double)var5, (double)(var9 + var6));
				var4.addVertex((double)(var8 + 0), (double)var5, (double)(var9 + var6));
				var4.draw();
			}
		}

		GL11.glEndList();
		this.glSkyList2 = this.starGLCallList + 2;
		GL11.glNewList(this.glSkyList2, 4864 /*GL_COMPILE*/);
		var5 = -16.0F;
		var4.startDrawingQuads();

		for(var8 = -var6 * var7; var8 <= var6 * var7; var8 += var6) {
			for(var9 = -var6 * var7; var9 <= var6 * var7; var9 += var6) {
				var4.addVertex((double)(var8 + var6), (double)var5, (double)(var9 + 0));
				var4.addVertex((double)(var8 + 0), (double)var5, (double)(var9 + 0));
				var4.addVertex((double)(var8 + 0), (double)var5, (double)(var9 + var6));
				var4.addVertex((double)(var8 + var6), (double)var5, (double)(var9 + var6));
			}
		}

		var4.draw();
		GL11.glEndList();
	}

	private void renderStars() {
		//BukkitContrib Start
		if (!BukkitContrib.getSkyManager().isStarsVisible()) {
			return;
		}
		//BukkitContrib End
		
		Random var1 = new Random(10842L);
		Tessellator var2 = Tessellator.instance;
		var2.startDrawingQuads();

		//BukkitContrib Start
		for(int i = 0; i < BukkitContrib.getSkyManager().getStarFrequency(); i++) {
		//BukkitContrib End
			double var4 = (double)(var1.nextFloat() * 2.0F - 1.0F);
			double var6 = (double)(var1.nextFloat() * 2.0F - 1.0F);
			double var8 = (double)(var1.nextFloat() * 2.0F - 1.0F);
			double var10 = (double)(0.25F + var1.nextFloat() * 0.25F);
			double var12 = var4 * var4 + var6 * var6 + var8 * var8;
			if(var12 < 1.0D && var12 > 0.01D) {
				var12 = 1.0D / Math.sqrt(var12);
				var4 *= var12;
				var6 *= var12;
				var8 *= var12;
				double var14 = var4 * 100.0D;
				double var16 = var6 * 100.0D;
				double var18 = var8 * 100.0D;
				double var20 = Math.atan2(var4, var8);
				double var22 = Math.sin(var20);
				double var24 = Math.cos(var20);
				double var26 = Math.atan2(Math.sqrt(var4 * var4 + var8 * var8), var6);
				double var28 = Math.sin(var26);
				double var30 = Math.cos(var26);
				double var32 = var1.nextDouble() * 3.141592653589793D * 2.0D;
				double var34 = Math.sin(var32);
				double var36 = Math.cos(var32);

				for(int var38 = 0; var38 < 4; ++var38) {
					double var39 = 0.0D;
					double var41 = (double)((var38 & 2) - 1) * var10;
					double var43 = (double)((var38 + 1 & 2) - 1) * var10;
					double var47 = var41 * var36 - var43 * var34;
					double var49 = var43 * var36 + var41 * var34;
					double var53 = var47 * var28 + var39 * var30;
					double var55 = var39 * var28 - var47 * var30;
					double var57 = var55 * var22 - var49 * var24;
					double var61 = var49 * var22 + var55 * var24;
					var2.addVertex(var14 + var57, var16 + var53, var18 + var61);
				}
			}
		}

		var2.draw();
	}

	public void changeWorld(World var1) {
		if(this.worldObj != null) {
			this.worldObj.removeWorldAccess(this);
		}

		this.prevSortX = -9999.0D;
		this.prevSortY = -9999.0D;
		this.prevSortZ = -9999.0D;
		RenderManager.instance.func_852_a(var1);
		this.worldObj = var1;
		this.globalRenderBlocks = new RenderBlocks(var1);
		if(var1 != null) {
			var1.addWorldAccess(this);
			this.loadRenderers();
		}

	}

	public void loadRenderers() {
		Block.leaves.setGraphicsLevel(this.mc.gameSettings.fancyGraphics);
		this.renderDistance = this.mc.gameSettings.renderDistance;
		int var1;
		if(this.worldRenderers != null) {
			for(var1 = 0; var1 < this.worldRenderers.length; ++var1) {
				this.worldRenderers[var1].func_1204_c();
			}
		}

		var1 = 64 << 3 - this.renderDistance;
		if(var1 > 400) {
			var1 = 400;
		}

		this.renderChunksWide = var1 / 16 + 1;
		this.renderChunksTall = 8;
		this.renderChunksDeep = var1 / 16 + 1;
		this.worldRenderers = new WorldRenderer[this.renderChunksWide * this.renderChunksTall * this.renderChunksDeep];
		this.sortedWorldRenderers = new WorldRenderer[this.renderChunksWide * this.renderChunksTall * this.renderChunksDeep];
		int var2 = 0;
		int var3 = 0;
		this.minBlockX = 0;
		this.minBlockY = 0;
		this.minBlockZ = 0;
		this.maxBlockX = this.renderChunksWide;
		this.maxBlockY = this.renderChunksTall;
		this.maxBlockZ = this.renderChunksDeep;

		int var4;
		for(var4 = 0; var4 < this.worldRenderersToUpdate.size(); ++var4) {
			((WorldRenderer)this.worldRenderersToUpdate.get(var4)).needsUpdate = false;
		}

		this.worldRenderersToUpdate.clear();
		this.tileEntities.clear();

		for(var4 = 0; var4 < this.renderChunksWide; ++var4) {
			for(int var5 = 0; var5 < this.renderChunksTall; ++var5) {
				for(int var6 = 0; var6 < this.renderChunksDeep; ++var6) {
					this.worldRenderers[(var6 * this.renderChunksTall + var5) * this.renderChunksWide + var4] = new WorldRenderer(this.worldObj, this.tileEntities, var4 * 16, var5 * 16, var6 * 16, 16, this.glRenderListBase + var2);
					if(this.occlusionEnabled) {
						this.worldRenderers[(var6 * this.renderChunksTall + var5) * this.renderChunksWide + var4].glOcclusionQuery = this.glOcclusionQueryBase.get(var3);
					}

					this.worldRenderers[(var6 * this.renderChunksTall + var5) * this.renderChunksWide + var4].isWaitingOnOcclusionQuery = false;
					this.worldRenderers[(var6 * this.renderChunksTall + var5) * this.renderChunksWide + var4].isVisible = true;
					this.worldRenderers[(var6 * this.renderChunksTall + var5) * this.renderChunksWide + var4].isInFrustum = true;
					this.worldRenderers[(var6 * this.renderChunksTall + var5) * this.renderChunksWide + var4].chunkIndex = var3++;
					this.worldRenderers[(var6 * this.renderChunksTall + var5) * this.renderChunksWide + var4].markDirty();
					this.sortedWorldRenderers[(var6 * this.renderChunksTall + var5) * this.renderChunksWide + var4] = this.worldRenderers[(var6 * this.renderChunksTall + var5) * this.renderChunksWide + var4];
					this.worldRenderersToUpdate.add(this.worldRenderers[(var6 * this.renderChunksTall + var5) * this.renderChunksWide + var4]);
					var2 += 3;
				}
			}
		}

		if(this.worldObj != null) {
			EntityLiving var7 = this.mc.renderViewEntity;
			if(var7 != null) {
				this.markRenderersForNewPosition(MathHelper.floor_double(var7.posX), MathHelper.floor_double(var7.posY), MathHelper.floor_double(var7.posZ));
				Arrays.sort(this.sortedWorldRenderers, new EntitySorter(var7));
			}
		}

		this.renderEntitiesStartupCounter = 2;
	}

	public void renderEntities(Vec3D var1, ICamera var2, float var3) {
		if(this.renderEntitiesStartupCounter > 0) {
			--this.renderEntitiesStartupCounter;
		} else {
			TileEntityRenderer.instance.cacheActiveRenderInfo(this.worldObj, this.renderEngine, this.mc.fontRenderer, this.mc.renderViewEntity, var3);
			RenderManager.instance.cacheActiveRenderInfo(this.worldObj, this.renderEngine, this.mc.fontRenderer, this.mc.renderViewEntity, this.mc.gameSettings, var3);
			this.countEntitiesTotal = 0;
			this.countEntitiesRendered = 0;
			this.countEntitiesHidden = 0;
			EntityLiving var4 = this.mc.renderViewEntity;
			RenderManager.renderPosX = var4.lastTickPosX + (var4.posX - var4.lastTickPosX) * (double)var3;
			RenderManager.renderPosY = var4.lastTickPosY + (var4.posY - var4.lastTickPosY) * (double)var3;
			RenderManager.renderPosZ = var4.lastTickPosZ + (var4.posZ - var4.lastTickPosZ) * (double)var3;
			TileEntityRenderer.staticPlayerX = var4.lastTickPosX + (var4.posX - var4.lastTickPosX) * (double)var3;
			TileEntityRenderer.staticPlayerY = var4.lastTickPosY + (var4.posY - var4.lastTickPosY) * (double)var3;
			TileEntityRenderer.staticPlayerZ = var4.lastTickPosZ + (var4.posZ - var4.lastTickPosZ) * (double)var3;
			List var5 = this.worldObj.getLoadedEntityList();
			this.countEntitiesTotal = var5.size();

			int var6;
			Entity var7;
			for(var6 = 0; var6 < this.worldObj.weatherEffects.size(); ++var6) {
				var7 = (Entity)this.worldObj.weatherEffects.get(var6);
				++this.countEntitiesRendered;
				if(var7.isInRangeToRenderVec3D(var1)) {
					RenderManager.instance.renderEntity(var7, var3);
				}
			}

			for(var6 = 0; var6 < var5.size(); ++var6) {
				var7 = (Entity)var5.get(var6);
				if(var7.isInRangeToRenderVec3D(var1) && (var7.ignoreFrustumCheck || var2.isBoundingBoxInFrustum(var7.boundingBox)) && (var7 != this.mc.renderViewEntity || this.mc.gameSettings.thirdPersonView || this.mc.renderViewEntity.isPlayerSleeping())) {
					int var8 = MathHelper.floor_double(var7.posY);
					if(var8 < 0) {
						var8 = 0;
					}

					if(var8 >= 128) {
						var8 = 127;
					}

					if(this.worldObj.blockExists(MathHelper.floor_double(var7.posX), var8, MathHelper.floor_double(var7.posZ))) {
						++this.countEntitiesRendered;
						RenderManager.instance.renderEntity(var7, var3);
					}
				}
			}

			for(var6 = 0; var6 < this.tileEntities.size(); ++var6) {
				TileEntityRenderer.instance.renderTileEntity((TileEntity)this.tileEntities.get(var6), var3);
			}

		}
	}

	public String getDebugInfoRenders() {
		return "C: " + this.renderersBeingRendered + "/" + this.renderersLoaded + ". F: " + this.renderersBeingClipped + ", O: " + this.renderersBeingOccluded + ", E: " + this.renderersSkippingRenderPass;
	}

	public String getDebugInfoEntities() {
		return "E: " + this.countEntitiesRendered + "/" + this.countEntitiesTotal + ". B: " + this.countEntitiesHidden + ", I: " + (this.countEntitiesTotal - this.countEntitiesHidden - this.countEntitiesRendered);
	}

	private void markRenderersForNewPosition(int var1, int var2, int var3) {
		var1 -= 8;
		var2 -= 8;
		var3 -= 8;
		this.minBlockX = Integer.MAX_VALUE;
		this.minBlockY = Integer.MAX_VALUE;
		this.minBlockZ = Integer.MAX_VALUE;
		this.maxBlockX = Integer.MIN_VALUE;
		this.maxBlockY = Integer.MIN_VALUE;
		this.maxBlockZ = Integer.MIN_VALUE;
		int var4 = this.renderChunksWide * 16;
		int var5 = var4 / 2;

		for(int var6 = 0; var6 < this.renderChunksWide; ++var6) {
			int var7 = var6 * 16;
			int var8 = var7 + var5 - var1;
			if(var8 < 0) {
				var8 -= var4 - 1;
			}

			var8 /= var4;
			var7 -= var8 * var4;
			if(var7 < this.minBlockX) {
				this.minBlockX = var7;
			}

			if(var7 > this.maxBlockX) {
				this.maxBlockX = var7;
			}

			for(int var9 = 0; var9 < this.renderChunksDeep; ++var9) {
				int var10 = var9 * 16;
				int var11 = var10 + var5 - var3;
				if(var11 < 0) {
					var11 -= var4 - 1;
				}

				var11 /= var4;
				var10 -= var11 * var4;
				if(var10 < this.minBlockZ) {
					this.minBlockZ = var10;
				}

				if(var10 > this.maxBlockZ) {
					this.maxBlockZ = var10;
				}

				for(int var12 = 0; var12 < this.renderChunksTall; ++var12) {
					int var13 = var12 * 16;
					if(var13 < this.minBlockY) {
						this.minBlockY = var13;
					}

					if(var13 > this.maxBlockY) {
						this.maxBlockY = var13;
					}

					WorldRenderer var14 = this.worldRenderers[(var9 * this.renderChunksTall + var12) * this.renderChunksWide + var6];
					boolean var15 = var14.needsUpdate;
					var14.setPosition(var7, var13, var10);
					if(!var15 && var14.needsUpdate) {
						this.worldRenderersToUpdate.add(var14);
					}
				}
			}
		}

	}

	public int sortAndRender(EntityLiving var1, int var2, double var3) {
		//BukkitContrib Start
		//Performance Change
		//Do not reload if we already can see farther than the new distance
		if(mc.gameSettings.renderDistance < renderDistance)
		{
			loadRenderers();
		}
		//Do not bother with this if we are resetting the renderers, loadRenderers clears the list anyway
		else {
			for(int var5 = 0; var5 < 10; ++var5) {
				this.worldRenderersCheckIndex = (this.worldRenderersCheckIndex + 1) % this.worldRenderers.length;
				WorldRenderer var6 = this.worldRenderers[this.worldRenderersCheckIndex];
				if(var6.needsUpdate && !this.worldRenderersToUpdate.contains(var6)) {
					this.worldRenderersToUpdate.add(var6);
				}
			}
		}
		//BukkitContrib End

		if(var2 == 0) {
			this.renderersLoaded = 0;
			this.renderersBeingClipped = 0;
			this.renderersBeingOccluded = 0;
			this.renderersBeingRendered = 0;
			this.renderersSkippingRenderPass = 0;
		}

		double var33 = var1.lastTickPosX + (var1.posX - var1.lastTickPosX) * var3;
		double var7 = var1.lastTickPosY + (var1.posY - var1.lastTickPosY) * var3;
		double var9 = var1.lastTickPosZ + (var1.posZ - var1.lastTickPosZ) * var3;
		double var11 = var1.posX - this.prevSortX;
		double var13 = var1.posY - this.prevSortY;
		double var15 = var1.posZ - this.prevSortZ;
		if(var11 * var11 + var13 * var13 + var15 * var15 > 16.0D) {
			this.prevSortX = var1.posX;
			this.prevSortY = var1.posY;
			this.prevSortZ = var1.posZ;
			this.markRenderersForNewPosition(MathHelper.floor_double(var1.posX), MathHelper.floor_double(var1.posY), MathHelper.floor_double(var1.posZ));
			Arrays.sort(this.sortedWorldRenderers, new EntitySorter(var1));
		}

		RenderHelper.disableStandardItemLighting();
		byte var17 = 0;
		int var34;
		if(this.occlusionEnabled && this.mc.gameSettings.advancedOpengl && !this.mc.gameSettings.anaglyph && var2 == 0) {
			byte var18 = 0;
			int var19 = 16;
			this.checkOcclusionQueryResult(var18, var19);

			for(int var20 = var18; var20 < var19; ++var20) {
				this.sortedWorldRenderers[var20].isVisible = true;
			}

			var34 = var17 + this.renderSortedRenderers(var18, var19, var2, var3);

			do {
				int var35 = var19;
				var19 *= 2;
				if(var19 > this.sortedWorldRenderers.length) {
					var19 = this.sortedWorldRenderers.length;
				}

				GL11.glDisable(3553 /*GL_TEXTURE_2D*/);
				GL11.glDisable(2896 /*GL_LIGHTING*/);
				GL11.glDisable(3008 /*GL_ALPHA_TEST*/);
				GL11.glDisable(2912 /*GL_FOG*/);
				GL11.glColorMask(false, false, false, false);
				GL11.glDepthMask(false);
				this.checkOcclusionQueryResult(var35, var19);
				GL11.glPushMatrix();
				float var36 = 0.0F;
				float var21 = 0.0F;
				float var22 = 0.0F;

				for(int var23 = var35; var23 < var19; ++var23) {
					if(this.sortedWorldRenderers[var23].skipAllRenderPasses()) {
						this.sortedWorldRenderers[var23].isInFrustum = false;
					} else {
						if(!this.sortedWorldRenderers[var23].isInFrustum) {
							this.sortedWorldRenderers[var23].isVisible = true;
						}

						if(this.sortedWorldRenderers[var23].isInFrustum && !this.sortedWorldRenderers[var23].isWaitingOnOcclusionQuery) {
							float var24 = MathHelper.sqrt_float(this.sortedWorldRenderers[var23].distanceToEntitySquared(var1));
							int var25 = (int)(1.0F + var24 / 128.0F);
							if(this.cloudOffsetX % var25 == var23 % var25) {
								WorldRenderer var26 = this.sortedWorldRenderers[var23];
								float var27 = (float)((double)var26.posXMinus - var33);
								float var28 = (float)((double)var26.posYMinus - var7);
								float var29 = (float)((double)var26.posZMinus - var9);
								float var30 = var27 - var36;
								float var31 = var28 - var21;
								float var32 = var29 - var22;
								if(var30 != 0.0F || var31 != 0.0F || var32 != 0.0F) {
									GL11.glTranslatef(var30, var31, var32);
									var36 += var30;
									var21 += var31;
									var22 += var32;
								}

								ARBOcclusionQuery.glBeginQueryARB('\u8914', this.sortedWorldRenderers[var23].glOcclusionQuery);
								this.sortedWorldRenderers[var23].callOcclusionQueryList();
								ARBOcclusionQuery.glEndQueryARB('\u8914');
								this.sortedWorldRenderers[var23].isWaitingOnOcclusionQuery = true;
							}
						}
					}
				}

				GL11.glPopMatrix();
				if(this.mc.gameSettings.anaglyph) {
					if(EntityRenderer.anaglyphField == 0) {
						GL11.glColorMask(false, true, true, true);
					} else {
						GL11.glColorMask(true, false, false, true);
					}
				} else {
					GL11.glColorMask(true, true, true, true);
				}

				GL11.glDepthMask(true);
				GL11.glEnable(3553 /*GL_TEXTURE_2D*/);
				GL11.glEnable(3008 /*GL_ALPHA_TEST*/);
				GL11.glEnable(2912 /*GL_FOG*/);
				var34 += this.renderSortedRenderers(var35, var19, var2, var3);
			} while(var19 < this.sortedWorldRenderers.length);
		} else {
			var34 = var17 + this.renderSortedRenderers(0, this.sortedWorldRenderers.length, var2, var3);
		}

		return var34;
	}

	private void checkOcclusionQueryResult(int var1, int var2) {
		for(int var3 = var1; var3 < var2; ++var3) {
			if(this.sortedWorldRenderers[var3].isWaitingOnOcclusionQuery) {
				this.occlusionResult.clear();
				ARBOcclusionQuery.glGetQueryObjectuARB(this.sortedWorldRenderers[var3].glOcclusionQuery, '\u8867', this.occlusionResult);
				if(this.occlusionResult.get(0) != 0) {
					this.sortedWorldRenderers[var3].isWaitingOnOcclusionQuery = false;
					this.occlusionResult.clear();
					ARBOcclusionQuery.glGetQueryObjectuARB(this.sortedWorldRenderers[var3].glOcclusionQuery, '\u8866', this.occlusionResult);
					this.sortedWorldRenderers[var3].isVisible = this.occlusionResult.get(0) != 0;
				}
			}
		}

	}

	private int renderSortedRenderers(int var1, int var2, int var3, double var4) {
		this.glRenderLists.clear();
		int var6 = 0;

		for(int var7 = var1; var7 < var2; ++var7) {
			if(var3 == 0) {
				++this.renderersLoaded;
				if(this.sortedWorldRenderers[var7].skipRenderPass[var3]) {
					++this.renderersSkippingRenderPass;
				} else if(!this.sortedWorldRenderers[var7].isInFrustum) {
					++this.renderersBeingClipped;
				} else if(this.occlusionEnabled && !this.sortedWorldRenderers[var7].isVisible) {
					++this.renderersBeingOccluded;
				} else {
					++this.renderersBeingRendered;
				}
			}

			if(!this.sortedWorldRenderers[var7].skipRenderPass[var3] && this.sortedWorldRenderers[var7].isInFrustum && (!this.occlusionEnabled || this.sortedWorldRenderers[var7].isVisible)) {
				int var8 = this.sortedWorldRenderers[var7].getGLCallListForPass(var3);
				if(var8 >= 0) {
					this.glRenderLists.add(this.sortedWorldRenderers[var7]);
					++var6;
				}
			}
		}

		EntityLiving var19 = this.mc.renderViewEntity;
		double var20 = var19.lastTickPosX + (var19.posX - var19.lastTickPosX) * var4;
		double var10 = var19.lastTickPosY + (var19.posY - var19.lastTickPosY) * var4;
		double var12 = var19.lastTickPosZ + (var19.posZ - var19.lastTickPosZ) * var4;
		int var14 = 0;

		int var15;
		for(var15 = 0; var15 < this.allRenderLists.length; ++var15) {
			this.allRenderLists[var15].func_859_b();
		}

		for(var15 = 0; var15 < this.glRenderLists.size(); ++var15) {
			WorldRenderer var16 = (WorldRenderer)this.glRenderLists.get(var15);
			int var17 = -1;

			for(int var18 = 0; var18 < var14; ++var18) {
				if(this.allRenderLists[var18].func_862_a(var16.posXMinus, var16.posYMinus, var16.posZMinus)) {
					var17 = var18;
				}
			}

			if(var17 < 0) {
				var17 = var14++;
				this.allRenderLists[var17].func_861_a(var16.posXMinus, var16.posYMinus, var16.posZMinus, var20, var10, var12);
			}

			this.allRenderLists[var17].func_858_a(var16.getGLCallListForPass(var3));
		}

		this.renderAllRenderLists(var3, var4);
		return var6;
	}

	public void renderAllRenderLists(int var1, double var2) {
		for(int var4 = 0; var4 < this.allRenderLists.length; ++var4) {
			this.allRenderLists[var4].func_860_a();
		}

	}

	public void updateClouds() {
		++this.cloudOffsetX;
	}

	public void renderSky(float var1) {
		if(!this.mc.theWorld.worldProvider.isNether) {
			GL11.glDisable(3553 /*GL_TEXTURE_2D*/);
			Vec3D var2 = this.worldObj.func_4079_a(this.mc.renderViewEntity, var1);
			float var3 = (float)var2.xCoord;
			float var4 = (float)var2.yCoord;
			float var5 = (float)var2.zCoord;
			float var7;
			float var8;
			if(this.mc.gameSettings.anaglyph) {
				float var6 = (var3 * 30.0F + var4 * 59.0F + var5 * 11.0F) / 100.0F;
				var7 = (var3 * 30.0F + var4 * 70.0F) / 100.0F;
				var8 = (var3 * 30.0F + var5 * 70.0F) / 100.0F;
				var3 = var6;
				var4 = var7;
				var5 = var8;
			}

			GL11.glColor3f(var3, var4, var5);
			Tessellator var17 = Tessellator.instance;
			GL11.glDepthMask(false);
			GL11.glEnable(2912 /*GL_FOG*/);
			GL11.glColor3f(var3, var4, var5);
			GL11.glCallList(this.glSkyList);
			GL11.glDisable(2912 /*GL_FOG*/);
			GL11.glDisable(3008 /*GL_ALPHA_TEST*/);
			GL11.glEnable(3042 /*GL_BLEND*/);
			GL11.glBlendFunc(770, 771);
			RenderHelper.disableStandardItemLighting();
			float[] var18 = this.worldObj.worldProvider.calcSunriseSunsetColors(this.worldObj.getCelestialAngle(var1), var1);
			float var9;
			float var10;
			float var11;
			float var12;
			if(var18 != null) {
				GL11.glDisable(3553 /*GL_TEXTURE_2D*/);
				GL11.glShadeModel(7425 /*GL_SMOOTH*/);
				GL11.glPushMatrix();
				GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
				var8 = this.worldObj.getCelestialAngle(var1);
				GL11.glRotatef(var8 > 0.5F?180.0F:0.0F, 0.0F, 0.0F, 1.0F);
				var9 = var18[0];
				var10 = var18[1];
				var11 = var18[2];
				float var14;
				if(this.mc.gameSettings.anaglyph) {
					var12 = (var9 * 30.0F + var10 * 59.0F + var11 * 11.0F) / 100.0F;
					float var13 = (var9 * 30.0F + var10 * 70.0F) / 100.0F;
					var14 = (var9 * 30.0F + var11 * 70.0F) / 100.0F;
					var9 = var12;
					var10 = var13;
					var11 = var14;
				}

				var17.startDrawing(6);
				var17.setColorRGBA_F(var9, var10, var11, var18[3]);
				var17.addVertex(0.0D, 100.0D, 0.0D);
				byte var19 = 16;
				var17.setColorRGBA_F(var18[0], var18[1], var18[2], 0.0F);

				for(int var20 = 0; var20 <= var19; ++var20) {
					var14 = (float)var20 * 3.1415927F * 2.0F / (float)var19;
					float var15 = MathHelper.sin(var14);
					float var16 = MathHelper.cos(var14);
					var17.addVertex((double)(var15 * 120.0F), (double)(var16 * 120.0F), (double)(-var16 * 40.0F * var18[3]));
				}

				var17.draw();
				GL11.glPopMatrix();
				GL11.glShadeModel(7424 /*GL_FLAT*/);
			}

			GL11.glEnable(3553 /*GL_TEXTURE_2D*/);
			GL11.glBlendFunc(770, 1);
			GL11.glPushMatrix();
			var7 = 1.0F - this.worldObj.getRainStrength(var1);
			var8 = 0.0F;
			var9 = 0.0F;
			var10 = 0.0F;
			GL11.glColor4f(1.0F, 1.0F, 1.0F, var7);
			GL11.glTranslatef(var8, var9, var10);
			GL11.glRotatef(0.0F, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(this.worldObj.getCelestialAngle(var1) * 360.0F, 1.0F, 0.0F, 0.0F);
			var11 = 30.0F;
			//BukkitContrib Start
			float f15 = var11;
			Tessellator tessellator = var17;
			if (BukkitContrib.getSkyManager().isSunVisible()) {
				if (BukkitContrib.getSkyManager().getSunTextureUrl() == null || CustomTextureManager.getTextureFromUrl(BukkitContrib.getSkyManager().getSunTextureUrl()) == null) {
					GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, renderEngine.getTexture("/terrain/sun.png"));
				}
				else {
					GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, renderEngine.getTexture(CustomTextureManager.getTextureFromUrl(BukkitContrib.getSkyManager().getSunTextureUrl())));
				}
				double multiplier = (BukkitContrib.getSkyManager().getSunSizePercent() / 100D);
				tessellator.startDrawingQuads();
				tessellator.addVertexWithUV(-f15, 100D / multiplier, -f15, 0.0D, 0.0D);
				tessellator.addVertexWithUV(f15, 100D / multiplier, -f15, 1.0D, 0.0D);
				tessellator.addVertexWithUV(f15, 100D / multiplier, f15, 1.0D, 1.0D);
				tessellator.addVertexWithUV(-f15, 100D / multiplier, f15, 0.0D, 1.0D);
				tessellator.draw();
			}
			f15 = 20F;
			if (BukkitContrib.getSkyManager().isMoonVisible()) {
				if (BukkitContrib.getSkyManager().getMoonTextureUrl() == null || CustomTextureManager.getTextureFromUrl(BukkitContrib.getSkyManager().getMoonTextureUrl()) == null) {
					GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, renderEngine.getTexture("/terrain/moon.png"));
				}
				else {
					GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, renderEngine.getTexture(CustomTextureManager.getTextureFromUrl(BukkitContrib.getSkyManager().getMoonTextureUrl())));
				}
				double multiplier = (BukkitContrib.getSkyManager().getMoonSizePercent() / 100D);
				tessellator.startDrawingQuads();
				tessellator.addVertexWithUV(-f15, -100D / multiplier, f15, 1.0D, 1.0D);
				tessellator.addVertexWithUV(f15, -100D / multiplier, f15, 0.0D, 1.0D);
				tessellator.addVertexWithUV(f15, -100D / multiplier, -f15, 0.0D, 0.0D);
				tessellator.addVertexWithUV(-f15, -100D / multiplier, -f15, 1.0D, 0.0D);
				tessellator.draw();
			}
			//BukkitContrib End
			GL11.glDisable(3553 /*GL_TEXTURE_2D*/);
			var12 = this.worldObj.getStarBrightness(var1) * var7;
			if(var12 > 0.0F) {
				GL11.glColor4f(var12, var12, var12, var12);
				GL11.glCallList(this.starGLCallList);
			}

			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glDisable(3042 /*GL_BLEND*/);
			GL11.glEnable(3008 /*GL_ALPHA_TEST*/);
			GL11.glEnable(2912 /*GL_FOG*/);
			GL11.glPopMatrix();
			if(this.worldObj.worldProvider.func_28112_c()) {
				GL11.glColor3f(var3 * 0.2F + 0.04F, var4 * 0.2F + 0.04F, var5 * 0.6F + 0.1F);
			} else {
				GL11.glColor3f(var3, var4, var5);
			}

			GL11.glDisable(3553 /*GL_TEXTURE_2D*/);
			GL11.glCallList(this.glSkyList2);
			GL11.glEnable(3553 /*GL_TEXTURE_2D*/);
			GL11.glDepthMask(true);
		}
	}

	public void renderClouds(float var1) {
		//BukkitContrib Start
		if (!BukkitContrib.getSkyManager().isCloudsVisible()) {
			return;
		}
		//BukkitContrib End
		if(!this.mc.theWorld.worldProvider.isNether) {
			if(this.mc.gameSettings.fancyGraphics) {
				this.renderCloudsFancy(var1);
			} else {
				GL11.glDisable(2884 /*GL_CULL_FACE*/);
				float var2 = (float)(this.mc.renderViewEntity.lastTickPosY + (this.mc.renderViewEntity.posY - this.mc.renderViewEntity.lastTickPosY) * (double)var1);
				byte var3 = 32;
				int var4 = 256 / var3;
				Tessellator var5 = Tessellator.instance;
				GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, this.renderEngine.getTexture("/environment/clouds.png"));
				GL11.glEnable(3042 /*GL_BLEND*/);
				GL11.glBlendFunc(770, 771);
				Vec3D var6 = this.worldObj.func_628_d(var1);
				float var7 = (float)var6.xCoord;
				float var8 = (float)var6.yCoord;
				float var9 = (float)var6.zCoord;
				float var10;
				if(this.mc.gameSettings.anaglyph) {
					var10 = (var7 * 30.0F + var8 * 59.0F + var9 * 11.0F) / 100.0F;
					float var11 = (var7 * 30.0F + var8 * 70.0F) / 100.0F;
					float var12 = (var7 * 30.0F + var9 * 70.0F) / 100.0F;
					var7 = var10;
					var8 = var11;
					var9 = var12;
				}

				var10 = 4.8828125E-4F;
				double var22 = this.mc.renderViewEntity.prevPosX + (this.mc.renderViewEntity.posX - this.mc.renderViewEntity.prevPosX) * (double)var1 + (double)(((float)this.cloudOffsetX + var1) * 0.03F);
				double var13 = this.mc.renderViewEntity.prevPosZ + (this.mc.renderViewEntity.posZ - this.mc.renderViewEntity.prevPosZ) * (double)var1;
				int var15 = MathHelper.floor_double(var22 / 2048.0D);
				int var16 = MathHelper.floor_double(var13 / 2048.0D);
				var22 -= (double)(var15 * 2048 /*GL_EXP*/);
				var13 -= (double)(var16 * 2048 /*GL_EXP*/);
				//BukkitContrib Start
				//float var17 = this.worldObj.worldProvider.getCloudHeight() - var2 + 0.33F;
				float var17 = BukkitContrib.getSkyManager().getCloudHeight() - var2 + 0.33F;
				//BukkitContrib End
				
				float var18 = (float)(var22 * (double)var10);
				float var19 = (float)(var13 * (double)var10);
				var5.startDrawingQuads();
				var5.setColorRGBA_F(var7, var8, var9, 0.8F);

				for(int var20 = -var3 * var4; var20 < var3 * var4; var20 += var3) {
					for(int var21 = -var3 * var4; var21 < var3 * var4; var21 += var3) {
						var5.addVertexWithUV((double)(var20 + 0), (double)var17, (double)(var21 + var3), (double)((float)(var20 + 0) * var10 + var18), (double)((float)(var21 + var3) * var10 + var19));
						var5.addVertexWithUV((double)(var20 + var3), (double)var17, (double)(var21 + var3), (double)((float)(var20 + var3) * var10 + var18), (double)((float)(var21 + var3) * var10 + var19));
						var5.addVertexWithUV((double)(var20 + var3), (double)var17, (double)(var21 + 0), (double)((float)(var20 + var3) * var10 + var18), (double)((float)(var21 + 0) * var10 + var19));
						var5.addVertexWithUV((double)(var20 + 0), (double)var17, (double)(var21 + 0), (double)((float)(var20 + 0) * var10 + var18), (double)((float)(var21 + 0) * var10 + var19));
					}
				}

				var5.draw();
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				GL11.glDisable(3042 /*GL_BLEND*/);
				GL11.glEnable(2884 /*GL_CULL_FACE*/);
			}
		}
	}

	public boolean func_27307_a(double var1, double var3, double var5, float var7) {
		return false;
	}

	public void renderCloudsFancy(float var1) {
		GL11.glDisable(2884 /*GL_CULL_FACE*/);
		float var2 = (float)(this.mc.renderViewEntity.lastTickPosY + (this.mc.renderViewEntity.posY - this.mc.renderViewEntity.lastTickPosY) * (double)var1);
		Tessellator var3 = Tessellator.instance;
		float var4 = 12.0F;
		float var5 = 4.0F;
		double var6 = (this.mc.renderViewEntity.prevPosX + (this.mc.renderViewEntity.posX - this.mc.renderViewEntity.prevPosX) * (double)var1 + (double)(((float)this.cloudOffsetX + var1) * 0.03F)) / (double)var4;
		double var8 = (this.mc.renderViewEntity.prevPosZ + (this.mc.renderViewEntity.posZ - this.mc.renderViewEntity.prevPosZ) * (double)var1) / (double)var4 + 0.33000001311302185D;
		//BukkitContrib Start
		//float var10 = this.worldObj.worldProvider.getCloudHeight() - var2 + 0.33F;
		float var10 = BukkitContrib.getSkyManager().getCloudHeight() - var2 + 0.33F;
		//BukkitContrib End
		int var11 = MathHelper.floor_double(var6 / 2048.0D);
		int var12 = MathHelper.floor_double(var8 / 2048.0D);
		var6 -= (double)(var11 * 2048 /*GL_EXP*/);
		var8 -= (double)(var12 * 2048 /*GL_EXP*/);
		GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, this.renderEngine.getTexture("/environment/clouds.png"));
		GL11.glEnable(3042 /*GL_BLEND*/);
		GL11.glBlendFunc(770, 771);
		Vec3D var13 = this.worldObj.func_628_d(var1);
		float var14 = (float)var13.xCoord;
		float var15 = (float)var13.yCoord;
		float var16 = (float)var13.zCoord;
		float var17;
		float var19;
		float var18;
		if(this.mc.gameSettings.anaglyph) {
			var17 = (var14 * 30.0F + var15 * 59.0F + var16 * 11.0F) / 100.0F;
			var18 = (var14 * 30.0F + var15 * 70.0F) / 100.0F;
			var19 = (var14 * 30.0F + var16 * 70.0F) / 100.0F;
			var14 = var17;
			var15 = var18;
			var16 = var19;
		}

		var17 = (float)(var6 * 0.0D);
		var18 = (float)(var8 * 0.0D);
		var19 = 0.00390625F;
		var17 = (float)MathHelper.floor_double(var6) * var19;
		var18 = (float)MathHelper.floor_double(var8) * var19;
		float var20 = (float)(var6 - (double)MathHelper.floor_double(var6));
		float var21 = (float)(var8 - (double)MathHelper.floor_double(var8));
		byte var22 = 8;
		byte var23 = 3;
		float var24 = 9.765625E-4F;
		GL11.glScalef(var4, 1.0F, var4);

		for(int var25 = 0; var25 < 2; ++var25) {
			if(var25 == 0) {
				GL11.glColorMask(false, false, false, false);
			} else if(this.mc.gameSettings.anaglyph) {
				if(EntityRenderer.anaglyphField == 0) {
					GL11.glColorMask(false, true, true, true);
				} else {
					GL11.glColorMask(true, false, false, true);
				}
			} else {
				GL11.glColorMask(true, true, true, true);
			}

			for(int var26 = -var23 + 1; var26 <= var23; ++var26) {
				for(int var27 = -var23 + 1; var27 <= var23; ++var27) {
					var3.startDrawingQuads();
					float var28 = (float)(var26 * var22);
					float var29 = (float)(var27 * var22);
					float var30 = var28 - var20;
					float var31 = var29 - var21;
					if(var10 > -var5 - 1.0F) {
						var3.setColorRGBA_F(var14 * 0.7F, var15 * 0.7F, var16 * 0.7F, 0.8F);
						var3.setNormal(0.0F, -1.0F, 0.0F);
						var3.addVertexWithUV((double)(var30 + 0.0F), (double)(var10 + 0.0F), (double)(var31 + (float)var22), (double)((var28 + 0.0F) * var19 + var17), (double)((var29 + (float)var22) * var19 + var18));
						var3.addVertexWithUV((double)(var30 + (float)var22), (double)(var10 + 0.0F), (double)(var31 + (float)var22), (double)((var28 + (float)var22) * var19 + var17), (double)((var29 + (float)var22) * var19 + var18));
						var3.addVertexWithUV((double)(var30 + (float)var22), (double)(var10 + 0.0F), (double)(var31 + 0.0F), (double)((var28 + (float)var22) * var19 + var17), (double)((var29 + 0.0F) * var19 + var18));
						var3.addVertexWithUV((double)(var30 + 0.0F), (double)(var10 + 0.0F), (double)(var31 + 0.0F), (double)((var28 + 0.0F) * var19 + var17), (double)((var29 + 0.0F) * var19 + var18));
					}

					if(var10 <= var5 + 1.0F) {
						var3.setColorRGBA_F(var14, var15, var16, 0.8F);
						var3.setNormal(0.0F, 1.0F, 0.0F);
						var3.addVertexWithUV((double)(var30 + 0.0F), (double)(var10 + var5 - var24), (double)(var31 + (float)var22), (double)((var28 + 0.0F) * var19 + var17), (double)((var29 + (float)var22) * var19 + var18));
						var3.addVertexWithUV((double)(var30 + (float)var22), (double)(var10 + var5 - var24), (double)(var31 + (float)var22), (double)((var28 + (float)var22) * var19 + var17), (double)((var29 + (float)var22) * var19 + var18));
						var3.addVertexWithUV((double)(var30 + (float)var22), (double)(var10 + var5 - var24), (double)(var31 + 0.0F), (double)((var28 + (float)var22) * var19 + var17), (double)((var29 + 0.0F) * var19 + var18));
						var3.addVertexWithUV((double)(var30 + 0.0F), (double)(var10 + var5 - var24), (double)(var31 + 0.0F), (double)((var28 + 0.0F) * var19 + var17), (double)((var29 + 0.0F) * var19 + var18));
					}

					var3.setColorRGBA_F(var14 * 0.9F, var15 * 0.9F, var16 * 0.9F, 0.8F);
					int var32;
					if(var26 > -1) {
						var3.setNormal(-1.0F, 0.0F, 0.0F);

						for(var32 = 0; var32 < var22; ++var32) {
							var3.addVertexWithUV((double)(var30 + (float)var32 + 0.0F), (double)(var10 + 0.0F), (double)(var31 + (float)var22), (double)((var28 + (float)var32 + 0.5F) * var19 + var17), (double)((var29 + (float)var22) * var19 + var18));
							var3.addVertexWithUV((double)(var30 + (float)var32 + 0.0F), (double)(var10 + var5), (double)(var31 + (float)var22), (double)((var28 + (float)var32 + 0.5F) * var19 + var17), (double)((var29 + (float)var22) * var19 + var18));
							var3.addVertexWithUV((double)(var30 + (float)var32 + 0.0F), (double)(var10 + var5), (double)(var31 + 0.0F), (double)((var28 + (float)var32 + 0.5F) * var19 + var17), (double)((var29 + 0.0F) * var19 + var18));
							var3.addVertexWithUV((double)(var30 + (float)var32 + 0.0F), (double)(var10 + 0.0F), (double)(var31 + 0.0F), (double)((var28 + (float)var32 + 0.5F) * var19 + var17), (double)((var29 + 0.0F) * var19 + var18));
						}
					}

					if(var26 <= 1) {
						var3.setNormal(1.0F, 0.0F, 0.0F);

						for(var32 = 0; var32 < var22; ++var32) {
							var3.addVertexWithUV((double)(var30 + (float)var32 + 1.0F - var24), (double)(var10 + 0.0F), (double)(var31 + (float)var22), (double)((var28 + (float)var32 + 0.5F) * var19 + var17), (double)((var29 + (float)var22) * var19 + var18));
							var3.addVertexWithUV((double)(var30 + (float)var32 + 1.0F - var24), (double)(var10 + var5), (double)(var31 + (float)var22), (double)((var28 + (float)var32 + 0.5F) * var19 + var17), (double)((var29 + (float)var22) * var19 + var18));
							var3.addVertexWithUV((double)(var30 + (float)var32 + 1.0F - var24), (double)(var10 + var5), (double)(var31 + 0.0F), (double)((var28 + (float)var32 + 0.5F) * var19 + var17), (double)((var29 + 0.0F) * var19 + var18));
							var3.addVertexWithUV((double)(var30 + (float)var32 + 1.0F - var24), (double)(var10 + 0.0F), (double)(var31 + 0.0F), (double)((var28 + (float)var32 + 0.5F) * var19 + var17), (double)((var29 + 0.0F) * var19 + var18));
						}
					}

					var3.setColorRGBA_F(var14 * 0.8F, var15 * 0.8F, var16 * 0.8F, 0.8F);
					if(var27 > -1) {
						var3.setNormal(0.0F, 0.0F, -1.0F);

						for(var32 = 0; var32 < var22; ++var32) {
							var3.addVertexWithUV((double)(var30 + 0.0F), (double)(var10 + var5), (double)(var31 + (float)var32 + 0.0F), (double)((var28 + 0.0F) * var19 + var17), (double)((var29 + (float)var32 + 0.5F) * var19 + var18));
							var3.addVertexWithUV((double)(var30 + (float)var22), (double)(var10 + var5), (double)(var31 + (float)var32 + 0.0F), (double)((var28 + (float)var22) * var19 + var17), (double)((var29 + (float)var32 + 0.5F) * var19 + var18));
							var3.addVertexWithUV((double)(var30 + (float)var22), (double)(var10 + 0.0F), (double)(var31 + (float)var32 + 0.0F), (double)((var28 + (float)var22) * var19 + var17), (double)((var29 + (float)var32 + 0.5F) * var19 + var18));
							var3.addVertexWithUV((double)(var30 + 0.0F), (double)(var10 + 0.0F), (double)(var31 + (float)var32 + 0.0F), (double)((var28 + 0.0F) * var19 + var17), (double)((var29 + (float)var32 + 0.5F) * var19 + var18));
						}
					}

					if(var27 <= 1) {
						var3.setNormal(0.0F, 0.0F, 1.0F);

						for(var32 = 0; var32 < var22; ++var32) {
							var3.addVertexWithUV((double)(var30 + 0.0F), (double)(var10 + var5), (double)(var31 + (float)var32 + 1.0F - var24), (double)((var28 + 0.0F) * var19 + var17), (double)((var29 + (float)var32 + 0.5F) * var19 + var18));
							var3.addVertexWithUV((double)(var30 + (float)var22), (double)(var10 + var5), (double)(var31 + (float)var32 + 1.0F - var24), (double)((var28 + (float)var22) * var19 + var17), (double)((var29 + (float)var32 + 0.5F) * var19 + var18));
							var3.addVertexWithUV((double)(var30 + (float)var22), (double)(var10 + 0.0F), (double)(var31 + (float)var32 + 1.0F - var24), (double)((var28 + (float)var22) * var19 + var17), (double)((var29 + (float)var32 + 0.5F) * var19 + var18));
							var3.addVertexWithUV((double)(var30 + 0.0F), (double)(var10 + 0.0F), (double)(var31 + (float)var32 + 1.0F - var24), (double)((var28 + 0.0F) * var19 + var17), (double)((var29 + (float)var32 + 0.5F) * var19 + var18));
						}
					}

					var3.draw();
				}
			}
		}

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(3042 /*GL_BLEND*/);
		GL11.glEnable(2884 /*GL_CULL_FACE*/);
	}

	public boolean updateRenderers(EntityLiving var1, boolean var2) {
		boolean var3 = false;
		if(var3) {
			Collections.sort(this.worldRenderersToUpdate, new RenderSorter(var1));
			int var17 = this.worldRenderersToUpdate.size() - 1;
			int var18 = this.worldRenderersToUpdate.size();

			for(int var19 = 0; var19 < var18; ++var19) {
				WorldRenderer var20 = (WorldRenderer)this.worldRenderersToUpdate.get(var17 - var19);
				if(!var2) {
					if(var20.distanceToEntitySquared(var1) > 256.0F) {
						if(var20.isInFrustum) {
							if(var19 >= 3) {
								return false;
							}
						} else if(var19 >= 1) {
							return false;
						}
					}
				} else if(!var20.isInFrustum) {
					continue;
				}

				var20.updateRenderer();
				this.worldRenderersToUpdate.remove(var20);
				var20.needsUpdate = false;
			}

			return this.worldRenderersToUpdate.size() == 0;
		} else {
			byte var4 = 2;
			RenderSorter var5 = new RenderSorter(var1);
			WorldRenderer[] var6 = new WorldRenderer[var4];
			ArrayList var7 = null;
			int var8 = this.worldRenderersToUpdate.size();
			int var9 = 0;

			int var10;
			WorldRenderer var11;
			int var12;
			int var13;
			label168:
			for(var10 = 0; var10 < var8; ++var10) {
				var11 = (WorldRenderer)this.worldRenderersToUpdate.get(var10);
				if(!var2) {
					if(var11.distanceToEntitySquared(var1) > 256.0F) {
						for(var12 = 0; var12 < var4 && (var6[var12] == null || var5.doCompare(var6[var12], var11) <= 0); ++var12) {
							;
						}

						--var12;
						if(var12 > 0) {
							var13 = var12;

							while(true) {
								--var13;
								if(var13 == 0) {
									var6[var12] = var11;
									continue label168;
								}

								var6[var13 - 1] = var6[var13];
							}
						}
						continue;
					}
				} else if(!var11.isInFrustum) {
					continue;
				}

				if(var7 == null) {
					var7 = new ArrayList();
				}

				++var9;
				var7.add(var11);
				this.worldRenderersToUpdate.set(var10, (Object)null);
			}

			if(var7 != null) {
				if(var7.size() > 1) {
					Collections.sort(var7, var5);
				}

				for(var10 = var7.size() - 1; var10 >= 0; --var10) {
					var11 = (WorldRenderer)var7.get(var10);
					var11.updateRenderer();
					var11.needsUpdate = false;
				}
			}

			var10 = 0;

			int var21;
			for(var21 = var4 - 1; var21 >= 0; --var21) {
				WorldRenderer var22 = var6[var21];
				if(var22 != null) {
					if(!var22.isInFrustum && var21 != var4 - 1) {
						var6[var21] = null;
						var6[0] = null;
						break;
					}

					var6[var21].updateRenderer();
					var6[var21].needsUpdate = false;
					++var10;
				}
			}

			var21 = 0;
			var12 = 0;

			for(var13 = this.worldRenderersToUpdate.size(); var21 != var13; ++var21) {
				WorldRenderer var14 = (WorldRenderer)this.worldRenderersToUpdate.get(var21);
				if(var14 != null) {
					boolean var15 = false;

					for(int var16 = 0; var16 < var4 && !var15; ++var16) {
						if(var14 == var6[var16]) {
							var15 = true;
						}
					}

					if(!var15) {
						if(var12 != var21) {
							this.worldRenderersToUpdate.set(var12, var14);
						}

						++var12;
					}
				}
			}

			while(true) {
				--var21;
				if(var21 < var12) {
					return var8 == var9 + var10;
				}

				this.worldRenderersToUpdate.remove(var21);
			}
		}
	}

	public void drawBlockBreaking(EntityPlayer var1, MovingObjectPosition var2, int var3, ItemStack var4, float var5) {
		Tessellator var6 = Tessellator.instance;
		GL11.glEnable(3042 /*GL_BLEND*/);
		GL11.glEnable(3008 /*GL_ALPHA_TEST*/);
		GL11.glBlendFunc(770, 1);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, (MathHelper.sin((float)System.currentTimeMillis() / 100.0F) * 0.2F + 0.4F) * 0.5F);
		int var8;
		if(var3 == 0) {
			if(this.damagePartialTime > 0.0F) {
				GL11.glBlendFunc(774, 768);
				int var7 = this.renderEngine.getTexture("/terrain.png");
				GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, var7);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
				GL11.glPushMatrix();
				var8 = this.worldObj.getBlockId(var2.blockX, var2.blockY, var2.blockZ);
				Block var9 = var8 > 0?Block.blocksList[var8]:null;
				GL11.glDisable(3008 /*GL_ALPHA_TEST*/);
				GL11.glPolygonOffset(-3.0F, -3.0F);
				GL11.glEnable('\u8037');
				double var10 = var1.lastTickPosX + (var1.posX - var1.lastTickPosX) * (double)var5;
				double var12 = var1.lastTickPosY + (var1.posY - var1.lastTickPosY) * (double)var5;
				double var14 = var1.lastTickPosZ + (var1.posZ - var1.lastTickPosZ) * (double)var5;
				if(var9 == null) {
					var9 = Block.stone;
				}

				GL11.glEnable(3008 /*GL_ALPHA_TEST*/);
				var6.startDrawingQuads();
				var6.setTranslationD(-var10, -var12, -var14);
				var6.disableColor();
				this.globalRenderBlocks.renderBlockUsingTexture(var9, var2.blockX, var2.blockY, var2.blockZ, 240 + (int)(this.damagePartialTime * 10.0F));
				var6.draw();
				var6.setTranslationD(0.0D, 0.0D, 0.0D);
				GL11.glDisable(3008 /*GL_ALPHA_TEST*/);
				GL11.glPolygonOffset(0.0F, 0.0F);
				GL11.glDisable('\u8037');
				GL11.glEnable(3008 /*GL_ALPHA_TEST*/);
				GL11.glDepthMask(true);
				GL11.glPopMatrix();
			}
		} else if(var4 != null) {
			GL11.glBlendFunc(770, 771);
			float var16 = MathHelper.sin((float)System.currentTimeMillis() / 100.0F) * 0.2F + 0.8F;
			GL11.glColor4f(var16, var16, var16, MathHelper.sin((float)System.currentTimeMillis() / 200.0F) * 0.2F + 0.5F);
			var8 = this.renderEngine.getTexture("/terrain.png");
			GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, var8);
			int var17 = var2.blockX;
			int var18 = var2.blockY;
			int var11 = var2.blockZ;
			if(var2.sideHit == 0) {
				--var18;
			}

			if(var2.sideHit == 1) {
				++var18;
			}

			if(var2.sideHit == 2) {
				--var11;
			}

			if(var2.sideHit == 3) {
				++var11;
			}

			if(var2.sideHit == 4) {
				--var17;
			}

			if(var2.sideHit == 5) {
				++var17;
			}
		}

		GL11.glDisable(3042 /*GL_BLEND*/);
		GL11.glDisable(3008 /*GL_ALPHA_TEST*/);
	}

	public void drawSelectionBox(EntityPlayer var1, MovingObjectPosition var2, int var3, ItemStack var4, float var5) {
		if(var3 == 0 && var2.typeOfHit == EnumMovingObjectType.TILE) {
			GL11.glEnable(3042 /*GL_BLEND*/);
			GL11.glBlendFunc(770, 771);
			GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.4F);
			GL11.glLineWidth(2.0F);
			GL11.glDisable(3553 /*GL_TEXTURE_2D*/);
			GL11.glDepthMask(false);
			float var6 = 0.0020F;
			int var7 = this.worldObj.getBlockId(var2.blockX, var2.blockY, var2.blockZ);
			if(var7 > 0) {
				Block.blocksList[var7].setBlockBoundsBasedOnState(this.worldObj, var2.blockX, var2.blockY, var2.blockZ);
				double var8 = var1.lastTickPosX + (var1.posX - var1.lastTickPosX) * (double)var5;
				double var10 = var1.lastTickPosY + (var1.posY - var1.lastTickPosY) * (double)var5;
				double var12 = var1.lastTickPosZ + (var1.posZ - var1.lastTickPosZ) * (double)var5;
				this.drawOutlinedBoundingBox(Block.blocksList[var7].getSelectedBoundingBoxFromPool(this.worldObj, var2.blockX, var2.blockY, var2.blockZ).expand((double)var6, (double)var6, (double)var6).getOffsetBoundingBox(-var8, -var10, -var12));
			}

			GL11.glDepthMask(true);
			GL11.glEnable(3553 /*GL_TEXTURE_2D*/);
			GL11.glDisable(3042 /*GL_BLEND*/);
		}

	}

	private void drawOutlinedBoundingBox(AxisAlignedBB var1) {
		Tessellator var2 = Tessellator.instance;
		var2.startDrawing(3);
		var2.addVertex(var1.minX, var1.minY, var1.minZ);
		var2.addVertex(var1.maxX, var1.minY, var1.minZ);
		var2.addVertex(var1.maxX, var1.minY, var1.maxZ);
		var2.addVertex(var1.minX, var1.minY, var1.maxZ);
		var2.addVertex(var1.minX, var1.minY, var1.minZ);
		var2.draw();
		var2.startDrawing(3);
		var2.addVertex(var1.minX, var1.maxY, var1.minZ);
		var2.addVertex(var1.maxX, var1.maxY, var1.minZ);
		var2.addVertex(var1.maxX, var1.maxY, var1.maxZ);
		var2.addVertex(var1.minX, var1.maxY, var1.maxZ);
		var2.addVertex(var1.minX, var1.maxY, var1.minZ);
		var2.draw();
		var2.startDrawing(1);
		var2.addVertex(var1.minX, var1.minY, var1.minZ);
		var2.addVertex(var1.minX, var1.maxY, var1.minZ);
		var2.addVertex(var1.maxX, var1.minY, var1.minZ);
		var2.addVertex(var1.maxX, var1.maxY, var1.minZ);
		var2.addVertex(var1.maxX, var1.minY, var1.maxZ);
		var2.addVertex(var1.maxX, var1.maxY, var1.maxZ);
		var2.addVertex(var1.minX, var1.minY, var1.maxZ);
		var2.addVertex(var1.minX, var1.maxY, var1.maxZ);
		var2.draw();
	}

	public void markBlocksForUpdate(int var1, int var2, int var3, int var4, int var5, int var6) {
		int var7 = MathHelper.bucketInt(var1, 16);
		int var8 = MathHelper.bucketInt(var2, 16);
		int var9 = MathHelper.bucketInt(var3, 16);
		int var10 = MathHelper.bucketInt(var4, 16);
		int var11 = MathHelper.bucketInt(var5, 16);
		int var12 = MathHelper.bucketInt(var6, 16);

		for(int var13 = var7; var13 <= var10; ++var13) {
			int var14 = var13 % this.renderChunksWide;
			if(var14 < 0) {
				var14 += this.renderChunksWide;
			}

			for(int var15 = var8; var15 <= var11; ++var15) {
				int var16 = var15 % this.renderChunksTall;
				if(var16 < 0) {
					var16 += this.renderChunksTall;
				}

				for(int var17 = var9; var17 <= var12; ++var17) {
					int var18 = var17 % this.renderChunksDeep;
					if(var18 < 0) {
						var18 += this.renderChunksDeep;
					}

					int var19 = (var18 * this.renderChunksTall + var16) * this.renderChunksWide + var14;
					WorldRenderer var20 = this.worldRenderers[var19];
					if(!var20.needsUpdate) {
						this.worldRenderersToUpdate.add(var20);
						var20.markDirty();
					}
				}
			}
		}

	}

	public void markBlockAndNeighborsNeedsUpdate(int var1, int var2, int var3) {
		this.markBlocksForUpdate(var1 - 1, var2 - 1, var3 - 1, var1 + 1, var2 + 1, var3 + 1);
	}

	public void markBlockRangeNeedsUpdate(int var1, int var2, int var3, int var4, int var5, int var6) {
		this.markBlocksForUpdate(var1 - 1, var2 - 1, var3 - 1, var4 + 1, var5 + 1, var6 + 1);
	}

	public void clipRenderersByFrustrum(ICamera var1, float var2) {
		for(int var3 = 0; var3 < this.worldRenderers.length; ++var3) {
			if(!this.worldRenderers[var3].skipAllRenderPasses() && (!this.worldRenderers[var3].isInFrustum || (var3 + this.frustrumCheckOffset & 15) == 0)) {
				this.worldRenderers[var3].updateInFrustrum(var1);
			}
		}

		++this.frustrumCheckOffset;
	}

	public void playRecord(String var1, int var2, int var3, int var4) {
		if(var1 != null) {
			this.mc.ingameGUI.setRecordPlayingMessage("C418 - " + var1);
		}

		this.mc.sndManager.playStreaming(var1, (float)var2, (float)var3, (float)var4, 1.0F, 1.0F);
	}

	public void playSound(String var1, double var2, double var4, double var6, float var8, float var9) {
		float var10 = 16.0F;
		if(var8 > 1.0F) {
			var10 *= var8;
		}

		if(this.mc.renderViewEntity.getDistanceSq(var2, var4, var6) < (double)(var10 * var10)) {
			this.mc.sndManager.playSound(var1, (float)var2, (float)var4, (float)var6, var8, var9);
		}

	}

	public void spawnParticle(String var1, double var2, double var4, double var6, double var8, double var10, double var12) {
		if(this.mc != null && this.mc.renderViewEntity != null && this.mc.effectRenderer != null) {
			double var14 = this.mc.renderViewEntity.posX - var2;
			double var16 = this.mc.renderViewEntity.posY - var4;
			double var18 = this.mc.renderViewEntity.posZ - var6;
			double var20 = 16.0D;
			if(var14 * var14 + var16 * var16 + var18 * var18 <= var20 * var20) {
				if(var1.equals("bubble")) {
					this.mc.effectRenderer.addEffect(new EntityBubbleFX(this.worldObj, var2, var4, var6, var8, var10, var12));
				} else if(var1.equals("smoke")) {
					this.mc.effectRenderer.addEffect(new EntitySmokeFX(this.worldObj, var2, var4, var6, var8, var10, var12));
				} else if(var1.equals("note")) {
					this.mc.effectRenderer.addEffect(new EntityNoteFX(this.worldObj, var2, var4, var6, var8, var10, var12));
				} else if(var1.equals("portal")) {
					this.mc.effectRenderer.addEffect(new EntityPortalFX(this.worldObj, var2, var4, var6, var8, var10, var12));
				} else if(var1.equals("explode")) {
					this.mc.effectRenderer.addEffect(new EntityExplodeFX(this.worldObj, var2, var4, var6, var8, var10, var12));
				} else if(var1.equals("flame")) {
					this.mc.effectRenderer.addEffect(new EntityFlameFX(this.worldObj, var2, var4, var6, var8, var10, var12));
				} else if(var1.equals("lava")) {
					this.mc.effectRenderer.addEffect(new EntityLavaFX(this.worldObj, var2, var4, var6));
				} else if(var1.equals("footstep")) {
					this.mc.effectRenderer.addEffect(new EntityFootStepFX(this.renderEngine, this.worldObj, var2, var4, var6));
				} else if(var1.equals("splash")) {
					this.mc.effectRenderer.addEffect(new EntitySplashFX(this.worldObj, var2, var4, var6, var8, var10, var12));
				} else if(var1.equals("largesmoke")) {
					this.mc.effectRenderer.addEffect(new EntitySmokeFX(this.worldObj, var2, var4, var6, var8, var10, var12, 2.5F));
				} else if(var1.equals("reddust")) {
					this.mc.effectRenderer.addEffect(new EntityReddustFX(this.worldObj, var2, var4, var6, (float)var8, (float)var10, (float)var12));
				} else if(var1.equals("snowballpoof")) {
					this.mc.effectRenderer.addEffect(new EntitySlimeFX(this.worldObj, var2, var4, var6, Item.snowball));
				} else if(var1.equals("snowshovel")) {
					this.mc.effectRenderer.addEffect(new EntitySnowShovelFX(this.worldObj, var2, var4, var6, var8, var10, var12));
				} else if(var1.equals("slime")) {
					this.mc.effectRenderer.addEffect(new EntitySlimeFX(this.worldObj, var2, var4, var6, Item.slimeBall));
				} else if(var1.equals("heart")) {
					this.mc.effectRenderer.addEffect(new EntityHeartFX(this.worldObj, var2, var4, var6, var8, var10, var12));
				}

			}
		}
	}

	public void obtainEntitySkin(Entity var1) {
		var1.updateCloak();
		if(var1.skinUrl != null) {
			this.renderEngine.obtainImageData(var1.skinUrl, new ImageBufferDownload());
		}

		if(var1.cloakUrl != null) {
			this.renderEngine.obtainImageData(var1.cloakUrl, new ImageBufferDownload());
		}

	}

	public void releaseEntitySkin(Entity var1) {
		if(var1.skinUrl != null) {
			this.renderEngine.releaseImageData(var1.skinUrl);
		}

		if(var1.cloakUrl != null) {
			this.renderEngine.releaseImageData(var1.cloakUrl);
		}

	}

	public void updateAllRenderers() {
		for(int var1 = 0; var1 < this.worldRenderers.length; ++var1) {
			if(this.worldRenderers[var1].isChunkLit && !this.worldRenderers[var1].needsUpdate) {
				this.worldRenderersToUpdate.add(this.worldRenderers[var1]);
				this.worldRenderers[var1].markDirty();
			}
		}

	}

	public void doNothingWithTileEntity(int var1, int var2, int var3, TileEntity var4) {}

	public void func_28137_f() {
		GLAllocation.func_28194_b(this.glRenderListBase);
	}

	public void playAuxSFX(EntityPlayer var1, int var2, int var3, int var4, int var5, int var6) {
		Random var7 = this.worldObj.rand;
		int var16;
		switch(var2) {
		case 1000:
			this.worldObj.playSoundEffect((double)var3, (double)var4, (double)var5, "random.click", 1.0F, 1.0F);
			break;
		case 1001:
			this.worldObj.playSoundEffect((double)var3, (double)var4, (double)var5, "random.click", 1.0F, 1.2F);
			break;
		case 1002:
			this.worldObj.playSoundEffect((double)var3, (double)var4, (double)var5, "random.bow", 1.0F, 1.2F);
			break;
		case 1003:
			if(Math.random() < 0.5D) {
				this.worldObj.playSoundEffect((double)var3 + 0.5D, (double)var4 + 0.5D, (double)var5 + 0.5D, "random.door_open", 1.0F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
			} else {
				this.worldObj.playSoundEffect((double)var3 + 0.5D, (double)var4 + 0.5D, (double)var5 + 0.5D, "random.door_close", 1.0F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
			}
			break;
		case 1004:
			this.worldObj.playSoundEffect((double)((float)var3 + 0.5F), (double)((float)var4 + 0.5F), (double)((float)var5 + 0.5F), "random.fizz", 0.5F, 2.6F + (var7.nextFloat() - var7.nextFloat()) * 0.8F);
			break;
		case 1005:
			if(Item.itemsList[var6] instanceof ItemRecord) {
				this.worldObj.playRecord(((ItemRecord)Item.itemsList[var6]).recordName, var3, var4, var5);
			} else {
				this.worldObj.playRecord((String)null, var3, var4, var5);
			}
			break;
		case 2000:
			int var8 = var6 % 3 - 1;
			int var9 = var6 / 3 % 3 - 1;
			double var10 = (double)var3 + (double)var8 * 0.6D + 0.5D;
			double var12 = (double)var4 + 0.5D;
			double var14 = (double)var5 + (double)var9 * 0.6D + 0.5D;

			for(var16 = 0; var16 < 10; ++var16) {
				double var31 = var7.nextDouble() * 0.2D + 0.01D;
				double var19 = var10 + (double)var8 * 0.01D + (var7.nextDouble() - 0.5D) * (double)var9 * 0.5D;
				double var21 = var12 + (var7.nextDouble() - 0.5D) * 0.5D;
				double var23 = var14 + (double)var9 * 0.01D + (var7.nextDouble() - 0.5D) * (double)var8 * 0.5D;
				double var25 = (double)var8 * var31 + var7.nextGaussian() * 0.01D;
				double var27 = -0.03D + var7.nextGaussian() * 0.01D;
				double var29 = (double)var9 * var31 + var7.nextGaussian() * 0.01D;
				this.spawnParticle("smoke", var19, var21, var23, var25, var27, var29);
			}

			return;
		case 2001:
			var16 = var6 & 255;
			if(var16 > 0) {
				Block var17 = Block.blocksList[var16];
				this.mc.sndManager.playSound(var17.stepSound.stepSoundDir(), (float)var3 + 0.5F, (float)var4 + 0.5F, (float)var5 + 0.5F, (var17.stepSound.getVolume() + 1.0F) / 2.0F, var17.stepSound.getPitch() * 0.8F);
			}

			this.mc.effectRenderer.addBlockDestroyEffects(var3, var4, var5, var6 & 255, var6 >> 8 & 255);
		}

	}
}
