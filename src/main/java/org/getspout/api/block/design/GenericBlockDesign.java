package org.getspout.api.block.design;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;

import org.getspout.api.block.Block;
import org.getspout.api.block.design.BlockDesign;
import org.getspout.api.block.design.Quad;
import org.getspout.api.block.design.Texture;
import org.getspout.api.block.design.Vertex;
import org.getspout.api.entity.Item;
import org.getspout.api.packet.PacketUtil;
import org.getspout.api.plugin.Plugin;
import org.getspout.api.util.MutableIntegerVector;

public class GenericBlockDesign implements BlockDesign {

	protected boolean reset = false;

	protected float lowXBound;
	protected float lowYBound;
	protected float lowZBound;
	protected float highXBound;
	protected float highYBound;
	protected float highZBound;

	protected String textureURL;
	protected String texturePlugin;
	
	protected Texture texture;

	protected float[][] xPos;
	protected float[][] yPos;
	protected float[][] zPos;

	protected float[][] textXPos;
	protected float[][] textYPos;

	protected int[] lightSourceXOffset;
	protected int[] lightSourceYOffset;
	protected int[] lightSourceZOffset;

	protected float maxBrightness = 1.0F;
	protected float minBrightness = 0F;

	protected float brightness = 0.5F;

	protected int renderPass = 0;

	public GenericBlockDesign() {
	}

	public GenericBlockDesign(float lowXBound, float lowYBound, float lowZBound, float highXBound, float highYBound, float highZBound, String textureURL, Plugin texturePlugin, float[][] xPos, float[][] yPos, float[][] zPos, float[][] textXPos, float[][] textYPos, int renderPass) {
		this.lowXBound = lowXBound;
		this.lowYBound = lowYBound;
		this.lowZBound = lowZBound;
		this.highXBound = highXBound;
		this.highYBound = highYBound;
		this.highZBound = highZBound;
		this.textureURL = textureURL;
		this.texturePlugin = texturePlugin.getDescription().getName();
		this.xPos = xPos;
		this.yPos = yPos;
		this.zPos = zPos;
		this.textXPos = textXPos;
		this.textYPos = textYPos;
		this.renderPass = renderPass;
	}
	
	public float[][] getX() {
		return xPos;
	}
	
	public float[][] getY() {
		return yPos;
	}
	
	public float[][] getZ() {
		return zPos;
	}
	
	public float[][] getTextureXPos() {
		return textXPos;
	}
	
	public float[][] getTextureYPos() {
		return textYPos;
	}
	
	public float getBrightness() {
		return brightness;
	}
	
	public float getMaxBrightness() {
		return maxBrightness;
	}
	
	public float getMinBrightness() {
		return minBrightness;
	}
	
	public float getLowXBound() {
		return lowXBound;
	}
	
	public float getLowYBound() {
		return lowYBound;
	}
	
	public float getLowZBound() {
		return lowZBound;
	}
	
	public float getHighXBound() {
		return highXBound;
	}
	
	public float getHighYBound() {
		return highYBound;
	}
	
	public float getHighZBound() {
		return highZBound;
	}

	public BlockDesign setMaxBrightness(float maxBrightness) {
		this.maxBrightness = maxBrightness;
		return this;
	}

	public BlockDesign setMinBrightness(float minBrightness) {
		this.minBrightness = minBrightness;
		return this;
	}

	public BlockDesign setBrightness(float brightness) {
		this.brightness = brightness * maxBrightness + (1 - brightness) * minBrightness;
		return this;
	}

	public BlockDesign setRenderPass(int renderPass) {
		this.renderPass = renderPass;
		return this;
	}

	public int getRenderPass() {
		return renderPass;
	}

	public int getNumBytes() {
		return PacketUtil.getNumBytes(textureURL) + PacketUtil.getNumBytes(texturePlugin) + PacketUtil.getDoubleArrayLength(xPos) + PacketUtil.getDoubleArrayLength(yPos) + PacketUtil.getDoubleArrayLength(zPos) + PacketUtil.getDoubleArrayLength(textXPos) + PacketUtil.getDoubleArrayLength(textYPos) + 9 * 4 + (3 + lightSourceXOffset.length + lightSourceXOffset.length + lightSourceXOffset.length) * 4;
	}

	public int getVersion() {
		return 3;
	}

	public void read(DataInputStream input) throws IOException {
		textureURL = PacketUtil.readString(input);
		if (textureURL.equals(resetString)) {
			reset = true;
			return;
		}
		reset = false;
		texturePlugin = PacketUtil.readString(input);
		xPos = PacketUtil.readDoubleArray(input);
		yPos = PacketUtil.readDoubleArray(input);
		zPos = PacketUtil.readDoubleArray(input);
		textXPos = PacketUtil.readDoubleArray(input);
		textYPos = PacketUtil.readDoubleArray(input);
		lowXBound = input.readFloat();
		lowYBound = input.readFloat();
		lowZBound = input.readFloat();
		highXBound = input.readFloat();
		highYBound = input.readFloat();
		highZBound = input.readFloat();
		maxBrightness = input.readFloat();
		minBrightness = input.readFloat();
		renderPass = input.readInt();
		lightSourceXOffset = PacketUtil.readIntArray(input);
		lightSourceYOffset = PacketUtil.readIntArray(input);
		lightSourceZOffset = PacketUtil.readIntArray(input);
	}

	private final static String resetString = "[reset]";

	public void writeReset(DataOutputStream output) {
		PacketUtil.writeString(output, resetString);
	}

	public int getResetNumBytes() {
		return PacketUtil.getNumBytes(resetString);
	}

	public void write(DataOutputStream output) throws IOException {
		if (reset) {
			PacketUtil.writeString(output, resetString);
			return;
		}
		PacketUtil.writeString(output, textureURL);
		PacketUtil.writeString(output, texturePlugin);
		PacketUtil.writeDoubleArray(output, xPos);
		PacketUtil.writeDoubleArray(output, yPos);
		PacketUtil.writeDoubleArray(output, zPos);
		PacketUtil.writeDoubleArray(output, textXPos);
		PacketUtil.writeDoubleArray(output, textYPos);
		output.writeFloat(lowXBound);
		output.writeFloat(lowYBound);
		output.writeFloat(lowZBound);
		output.writeFloat(highXBound);
		output.writeFloat(highYBound);
		output.writeFloat(highZBound);
		output.writeFloat(maxBrightness);
		output.writeFloat(minBrightness);
		output.writeInt(renderPass);
		PacketUtil.writeIntArray(output, lightSourceXOffset);
		PacketUtil.writeIntArray(output, lightSourceYOffset);
		PacketUtil.writeIntArray(output, lightSourceZOffset);
	}

	public BlockDesign setTexture(Plugin plugin, String textureURL) {
		this.texturePlugin = plugin.getDescription().getName();
		this.textureURL = textureURL;
		return this;
	}

	public BlockDesign setBoundingBox(float lowX, float lowY, float lowZ, float highX, float highY, float highZ) {
		this.lowXBound = lowX;
		this.lowYBound = lowY;
		this.lowZBound = lowZ;
		this.highXBound = highX;
		this.highYBound = highY;
		this.highZBound = highZ;
		return this;
	}

	public BlockDesign setQuadNumber(int quads) {
		xPos = new float[quads][];
		yPos = new float[quads][];
		zPos = new float[quads][];
		textXPos = new float[quads][];
		textYPos = new float[quads][];
		lightSourceXOffset = new int[quads];
		lightSourceYOffset = new int[quads];
		lightSourceZOffset = new int[quads];

		for (int i = 0; i < quads; i++) {
			xPos[i] = new float[4];
			yPos[i] = new float[4];
			zPos[i] = new float[4];
			textXPos[i] = new float[4];
			textYPos[i] = new float[4];
			lightSourceXOffset[i] = 0;
			lightSourceYOffset[i] = 0;
			lightSourceZOffset[i] = 0;
		}
		return this;
	}

	public BlockDesign setQuad(int quadNumber, float x1, float y1, float z1, int tx1, int ty1, float x2, float y2, float z2, int tx2, int ty2, float x3, float y3, float z3, int tx3, int ty3, float x4, float y4, float z4, int tx4, int ty4, int textureSizeX, int textureSizeY) {

		setVertex(quadNumber, 0, x1, y1, z1, tx1, ty1, textureSizeX, textureSizeY);
		setVertex(quadNumber, 1, x2, y2, z2, tx2, ty2, textureSizeX, textureSizeY);
		setVertex(quadNumber, 2, x3, y3, z3, tx3, ty3, textureSizeX, textureSizeY);
		setVertex(quadNumber, 3, x4, y4, z4, tx4, ty4, textureSizeX, textureSizeY);
		return this;

	}

	public BlockDesign setVertex(int quadNumber, int vertexNumber, float x, float y, float z, int tx, int ty, int textureSizeX, int textureSizeY) {
		xPos[quadNumber][vertexNumber] = x;
		yPos[quadNumber][vertexNumber] = y;
		zPos[quadNumber][vertexNumber] = z;
		textXPos[quadNumber][vertexNumber] = (float) tx / (float) textureSizeX;
		textYPos[quadNumber][vertexNumber] = (float) ty / (float) textureSizeY;
		return this;
	}

	public String getTexureURL() {
		return textureURL;
	}

	public String getTexturePlugin() {
		return texturePlugin;
	}

	public boolean isReset() {
		return reset;
	}

	public BlockDesign setLightSource(int quad, int x, int y, int z) {
		lightSourceXOffset[quad] = x;
		lightSourceYOffset[quad] = y;
		lightSourceZOffset[quad] = z;
		return this;
	}

	public MutableIntegerVector getLightSource(int quad, int x, int y, int z) {
		MutableIntegerVector blockVector = new MutableIntegerVector(x + lightSourceXOffset[quad], y + lightSourceYOffset[quad], z + lightSourceZOffset[quad]);
		return blockVector;
	}

	public BlockDesign setTexture(Plugin plugin, Texture texture) {
		this.texture = texture;
		return setTexture(plugin, texture.getTexture());
	}
	
	public Texture getTexture() {
		return texture;
	}

	public BlockDesign setQuad(Quad quad) {
		return setVertex(quad.getVertex(0)).setVertex(quad.getVertex(1)).setVertex(quad.getVertex(2)).setVertex(quad.getVertex(3));
	}

	public BlockDesign setVertex(Vertex vertex) {
		return setVertex(vertex.getQuadNum(), vertex.getIndex(), vertex.getX(), vertex.getY(), vertex.getZ(), vertex.getTextureX(), vertex.getTextureY(), vertex.getTextureWidth(), vertex.getTextureHeight());
	}

	public boolean renderBlock(Block block, int x, int y, int z) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean renderItemstack(Item item, float x, float y, float depth, float rotation, float scale, Random rand) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean renderItemOnHUD(float x, float y, float depth) {
		// TODO Auto-generated method stub
		return false;
	}
}
