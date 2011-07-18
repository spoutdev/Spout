package net.minecraft.src;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Block;
import net.minecraft.src.Chunk;
import net.minecraft.src.ChunkCache;
import net.minecraft.src.Entity;
import net.minecraft.src.ICamera;
import net.minecraft.src.MathHelper;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.RenderItem;
import net.minecraft.src.Tessellator;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntityRenderer;
import net.minecraft.src.World;
import org.lwjgl.opengl.GL11;

public class WorldRenderer {

	public World worldObj;
	private int glRenderList = -1;
	private static Tessellator tessellator = Tessellator.instance;
	public static int chunksUpdated = 0;
	public int posX;
	public int posY;
	public int posZ;
	public int sizeWidth;
	public int sizeHeight;
	public int sizeDepth;
	public int posXMinus;
	public int posYMinus;
	public int posZMinus;
	public int posXClip;
	public int posYClip;
	public int posZClip;
	public boolean isInFrustum = false;
	public boolean[] skipRenderPass = new boolean[2];
	public int posXPlus;
	public int posYPlus;
	public int posZPlus;
	public float rendererRadius;
	public boolean needsUpdate;
	public AxisAlignedBB rendererBoundingBox;
	public int chunkIndex;
	public boolean isVisible = true;
	public boolean isWaitingOnOcclusionQuery;
	public int glOcclusionQuery;
	public boolean isChunkLit;
	private boolean isInitialized = false;
	public List tileEntityRenderers = new ArrayList();
	private List tileEntities;


	public WorldRenderer(World var1, List var2, int var3, int var4, int var5, int var6, int var7) {
		this.worldObj = var1;
		this.tileEntities = var2;
		this.sizeWidth = this.sizeHeight = this.sizeDepth = var6;
		this.rendererRadius = MathHelper.sqrt_float((float)(this.sizeWidth * this.sizeWidth + this.sizeHeight * this.sizeHeight + this.sizeDepth * this.sizeDepth)) / 2.0F;
		this.glRenderList = var7;
		this.posX = -999;
		this.setPosition(var3, var4, var5);
		this.needsUpdate = false;
	}

	public void setPosition(int var1, int var2, int var3) {
		if(var1 != this.posX || var2 != this.posY || var3 != this.posZ) {
			this.setDontDraw();
			this.posX = var1;
			this.posY = var2;
			this.posZ = var3;
			this.posXPlus = var1 + this.sizeWidth / 2;
			this.posYPlus = var2 + this.sizeHeight / 2;
			this.posZPlus = var3 + this.sizeDepth / 2;
			this.posXClip = var1 & 1023;
			this.posYClip = var2;
			this.posZClip = var3 & 1023;
			this.posXMinus = var1 - this.posXClip;
			this.posYMinus = var2 - this.posYClip;
			this.posZMinus = var3 - this.posZClip;
			float var4 = 6.0F;
			this.rendererBoundingBox = AxisAlignedBB.getBoundingBox((double)((float)var1 - var4), (double)((float)var2 - var4), (double)((float)var3 - var4), (double)((float)(var1 + this.sizeWidth) + var4), (double)((float)(var2 + this.sizeHeight) + var4), (double)((float)(var3 + this.sizeDepth) + var4));
			GL11.glNewList(this.glRenderList + 2, 4864 /*GL_COMPILE*/);
			RenderItem.renderAABB(AxisAlignedBB.getBoundingBoxFromPool((double)((float)this.posXClip - var4), (double)((float)this.posYClip - var4), (double)((float)this.posZClip - var4), (double)((float)(this.posXClip + this.sizeWidth) + var4), (double)((float)(this.posYClip + this.sizeHeight) + var4), (double)((float)(this.posZClip + this.sizeDepth) + var4)));
			GL11.glEndList();
			this.markDirty();
		}
	}

	private void setupGLTranslation() {
		GL11.glTranslatef((float)this.posXClip, (float)this.posYClip, (float)this.posZClip);
	}

	public void updateRenderer() {
		if(this.needsUpdate) {
			++chunksUpdated;
			int var1 = this.posX;
			int var2 = this.posY;
			int var3 = this.posZ;
			int var4 = this.posX + this.sizeWidth;
			int var5 = this.posY + this.sizeHeight;
			int var6 = this.posZ + this.sizeDepth;

			for(int var7 = 0; var7 < 2; ++var7) {
				this.skipRenderPass[var7] = true;
			}

			Chunk.isLit = false;
			HashSet var21 = new HashSet();
			var21.addAll(this.tileEntityRenderers);
			this.tileEntityRenderers.clear();
			byte var8 = 1;
			ChunkCache var9 = new ChunkCache(this.worldObj, var1 - var8, var2 - var8, var3 - var8, var4 + var8, var5 + var8, var6 + var8);
			RenderBlocks var10 = new RenderBlocks(var9);

			for(int var11 = 0; var11 < 2; ++var11) {
				boolean var12 = false;
				boolean var13 = false;
				boolean var14 = false;

				for(int var15 = var2; var15 < var5; ++var15) {
					for(int var16 = var3; var16 < var6; ++var16) {
						for(int var17 = var1; var17 < var4; ++var17) {
							int var18 = var9.getBlockId(var17, var15, var16);
							if(var18 > 0) {
								if(!var14) {
									var14 = true;
									GL11.glNewList(this.glRenderList + var11, 4864 /*GL_COMPILE*/);
									GL11.glPushMatrix();
									this.setupGLTranslation();
									float var19 = 1.000001F;
									GL11.glTranslatef((float)(-this.sizeDepth) / 2.0F, (float)(-this.sizeHeight) / 2.0F, (float)(-this.sizeDepth) / 2.0F);
									GL11.glScalef(var19, var19, var19);
									GL11.glTranslatef((float)this.sizeDepth / 2.0F, (float)this.sizeHeight / 2.0F, (float)this.sizeDepth / 2.0F);
									//BukkitContrib Performance Start
									tessellator.isLoadingChunk = true;
									//BukkitContrib Performance End
									tessellator.startDrawingQuads();
									tessellator.setTranslationD((double)(-this.posX), (double)(-this.posY), (double)(-this.posZ));
								}

								if(var11 == 0 && Block.isBlockContainer[var18]) {
									TileEntity var23 = var9.getBlockTileEntity(var17, var15, var16);
									if(TileEntityRenderer.instance.hasSpecialRenderer(var23)) {
										this.tileEntityRenderers.add(var23);
									}
								}

								Block var24 = Block.blocksList[var18];
								int var20 = var24.getRenderBlockPass();
								if(var20 != var11) {
									var12 = true;
								} else if(var20 == var11) {
									var13 |= var10.renderBlockByRenderType(var24, var17, var15, var16);
								}
							}
						}
					}
				}

				if(var14) {
					tessellator.draw();
					GL11.glPopMatrix();
					GL11.glEndList();
					//BukkitContrib Performance Start
					tessellator.isLoadingChunk = false;
					//BukkitContrib Performance End
					tessellator.setTranslationD(0.0D, 0.0D, 0.0D);
				} else {
					var13 = false;
				}

				if(var13) {
					this.skipRenderPass[var11] = false;
				}

				if(!var12) {
					break;
				}
			}

			HashSet var22 = new HashSet();
			var22.addAll(this.tileEntityRenderers);
			var22.removeAll(var21);
			this.tileEntities.addAll(var22);
			var21.removeAll(this.tileEntityRenderers);
			this.tileEntities.removeAll(var21);
			this.isChunkLit = Chunk.isLit;
			this.isInitialized = true;
		}
	}

	public float distanceToEntitySquared(Entity var1) {
		float var2 = (float)(var1.posX - (double)this.posXPlus);
		float var3 = (float)(var1.posY - (double)this.posYPlus);
		float var4 = (float)(var1.posZ - (double)this.posZPlus);
		return var2 * var2 + var3 * var3 + var4 * var4;
	}

	public void setDontDraw() {
		for(int var1 = 0; var1 < 2; ++var1) {
			this.skipRenderPass[var1] = true;
		}

		this.isInFrustum = false;
		this.isInitialized = false;
	}

	public void func_1204_c() {
		this.setDontDraw();
		this.worldObj = null;
	}

	public int getGLCallListForPass(int var1) {
		return !this.isInFrustum?-1:(!this.skipRenderPass[var1]?this.glRenderList + var1:-1);
	}

	public void updateInFrustrum(ICamera var1) {
		this.isInFrustum = var1.isBoundingBoxInFrustum(this.rendererBoundingBox);
	}

	public void callOcclusionQueryList() {
		GL11.glCallList(this.glRenderList + 2);
	}

	public boolean skipAllRenderPasses() {
		return !this.isInitialized?false:this.skipRenderPass[0] && this.skipRenderPass[1];
	}

	public void markDirty() {
		this.needsUpdate = true;
	}

}
