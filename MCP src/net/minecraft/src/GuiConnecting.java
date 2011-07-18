package net.minecraft.src;

import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiMainMenu;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.NetClientHandler;
import net.minecraft.src.StringTranslate;
import net.minecraft.src.ThreadConnectToServer;
import net.minecraft.src.World;

public class GuiConnecting extends GuiScreen {

	private NetClientHandler clientHandler;
	private boolean cancelled = false;

	//BukkitContrib Start
	private int counter = 0;
	private String currentMsg = null;
	private final String[] highlyInformativeMessages = {
		"Adding Hidden Agendas ",
		"Adjusting Bell Curves ",
		"Aesthesizing Industrial Areas ",
		"Aligning Covariance Matrices ",
		"Applying Feng Shui Shaders ",
		"Applying Theatre Soda Layer ",
		"Asserting Packed Exemplars ",
		"Attempting to Lock Back-Buffer ",
		"Binding Sapling Root System ",
		"Breeding Fauna ",
		"Building Data Trees ",
		"Bureacritizing Bureaucracies ",
		"Calculating Inverse Probability Matrices ",
		"Calculating Llama Expectoration Trajectory ",
		"Calibrating Blue Skies ",
		"Charging Ozone Layer ",
		"Coalescing Cloud Formations ",
		"Cohorting Exemplars ",
		"Collecting Meteor Particles ",
		"Compounding Inert Tessellations ",
		"Compressing Fish Files ",
		"Computing Optimal Bin Packing ",
		"Concatenating Sub-Contractors ",
		"Containing Existential Buffer ",
		"Dailing Mother ",
		"Debarking Ark Ramp ",
		"Debunching Unionized Commercial Services ",
		"Deciding What Message to Display Next ",
		"Decomposing Singular Values ",
		"Decrementing Tectonic Plates ",
		"Deleting Ferry Routes ",
		"Depixelating Inner Mountain Surface Back Faces ",
		"Depositing Slush Funds ",
		"Destabilizing Economic Indicators ",
		"Determining Width of Blast Fronts ",
		"Deunionizing Bulldozers ",
		"Dicing Models ",
		"Diluting Livestock Nutrition Variables ",
		"Doing A Barrel Roll ",
		"Downloading Satellite Terrain Data ",
		"Doubting The Spoon ",
		"Exposing Flash Variables to Streak System ",
		"Extracting Resources ",
		"Factoring Pay Scale ",
		"Fixing Election Outcome Matrix ",
		"Flood-Filling Ground Water ",
		"Flushing Pipe Network ",
		"Gathering Particle Sources ",
		"Generating Jobs ",
		"Gesticulating Mimes ",
		"Graphing Whale Migration ",
		"Hiding Willio Webnet Mask ",
		"Hiring Consultant ",
		"Implementing Impeachment Routine ",
		"Increasing Accuracy of Memory Leaks ",
		"Increasing Magmafacation ",
		"Initializing Tracking Mechanism ",
		"Initializing Breeding Timetable ",
		"Initializing Robotic Click-Path AI ",
		"Inserting Sublimated Messages ",
		"Integrating Curves ",
		"Integrating Illumination Form Factors ",
		"Integrating Population Graphs ",
		"Iterating Cellular Automata ",
		"Lecturing Errant Subsystems ",
		"Losing The Game ",
		"Mixing Genetic Pool ",
		"Modeling Object Components ",
		"Mopping Occupant Leaks ",
		"Normalizing Power ",
		"Obfuscating Quigley Matrix ",
		"Perturbing Matrices ",
		"Pixalating Nude Patch ",
		"Polishing Water Highlights ",
		"Populating Block Templates ",
		"Preparing Sprites for Random Walks ",
		"Prioritizing Slimes ",
		"Projecting Law Enforcement Pastry Intake ",
		"Promising Cake ",
		"Realigning Alternate Time Frames ",
		"Reconfiguring User Mental Processes ",
		"Relaxing Splines ",
		"Removing Texture Gradients ",
		"Restoring World From Backups ",
		"Resolving GUID Conflict ",
		"Reticulating Splines ",
		"Retracting Phong Shader ",
		"Retrieving from Back Store ",
		"Reverse Engineering Image Consultant ",
		"Routing Neural Network Infanstructure ",
		"Scattering Rhino Food Sources ",
		"Scrubbing Terrain ",
		"Searching for Llamas ",
		"Seeding Architecture Simulation Parameters ",
		"Sequencing Particles ",
		"Setting Advisor Moods ",
		"Setting Inner Deity Indicators ",
		"Setting Universal Physical Constants ",
		"Sonically Enhancing Occupant-Free Timber ",
		"Speculating Stock Market Indices ",
		"Splatting Transforms ",
		"Stratifying Ground Layers ",
		"Sub-Sampling Water Data ",
		"Synthesizing Gravity", 
		"Synthesizing Wavelets",
		"Time-Compressing Simulator Clock",
		"Unable to Reveal Current Activity",
		"Wanting To Believe ",
		"Weathering Landforms",
		"Zeroing Creeper Network"
	};
	//BukkitContrib End
	public GuiConnecting(Minecraft var1, String var2, int var3) {
		System.out.println("Connecting to " + var2 + ", " + var3);
		var1.changeWorld1((World)null);
		(new ThreadConnectToServer(this, var1, var2, var3)).start();
	}

	public void updateScreen() {
		if(this.clientHandler != null) {
			this.clientHandler.processReadPackets();
		}

	}

	protected void keyTyped(char var1, int var2) {}

	public void initGui() {
		StringTranslate var1 = StringTranslate.getInstance();
		this.controlList.clear();
		this.controlList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120 + 12, var1.translateKey("gui.cancel")));
	}

	protected void actionPerformed(GuiButton var1) {
		if(var1.id == 0) {
			this.cancelled = true;
			if(this.clientHandler != null) {
				this.clientHandler.disconnect();
			}

			this.mc.displayGuiScreen(new GuiMainMenu());
		}

	}

	public void drawScreen(int var1, int var2, float var3) {
		this.drawDefaultBackground();
		StringTranslate var4 = StringTranslate.getInstance();
		if(this.clientHandler == null) {
			this.drawCenteredString(this.fontRenderer, var4.translateKey("connect.connecting"), this.width / 2, this.height / 2 - 50, 16777215);
			this.drawCenteredString(this.fontRenderer, "", this.width / 2, this.height / 2 - 10, 16777215);
		} else {
			this.drawCenteredString(this.fontRenderer, var4.translateKey("connect.authorizing"), this.width / 2, this.height / 2 - 50, 16777215);
			this.drawCenteredString(this.fontRenderer, this.clientHandler.field_1209_a, this.width / 2, this.height / 2 - 10, 16777215);
		}
		//BukkitContrib Start
		if (counter == 4500 || currentMsg == null) {
			counter = 0;
			currentMsg = highlyInformativeMessages[(new java.util.Random()).nextInt(highlyInformativeMessages.length)];
			currentMsg = org.bukkit.ChatColor.GREEN.toString() + currentMsg.trim() + "...";
		}
		else {
			counter++;
		}
		drawString(fontRenderer, currentMsg, 7, height / 2 - 115, 0xffffff);
		//BukkitContrib End
		super.drawScreen(var1, var2, var3);
	}

	// $FF: synthetic method
	static NetClientHandler setNetClientHandler(GuiConnecting var0, NetClientHandler var1) {
		return var0.clientHandler = var1;
	}

	// $FF: synthetic method
	static boolean isCancelled(GuiConnecting var0) {
		return var0.cancelled;
	}

	// $FF: synthetic method
	static NetClientHandler getNetClientHandler(GuiConnecting var0) {
		return var0.clientHandler;
	}
}
