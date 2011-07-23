package net.minecraft.src;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import org.bukkitcontrib.io.FileUtil;
//BukkitContrib

public class DataMiningThread extends Thread{
	private volatile boolean onLogin = false;
	private volatile boolean multiplayer = false;
	private boolean runOnce = false;
	
	public void onLogin() {
		onLogin = true;
	}
	
	private void doLogin() {
		onRunOnce();
		if (BukkitContrib.getVersion() != -1) {
			onBukkitContribLogin();
		}
		else {
			onVanillaLogin();
		}
	}
	
	private void doSinglePlayer() {
		onRunOnce();
		pingLink("http://bit.ly/bukkitcontribsingleplayer");
	}
	
	private void onRunOnce() {
		File runOnce = new File(FileUtil.getCacheDirectory(), "runonce");
		if (!runOnce.exists()) {
			try {
				runOnce.createNewFile();
				pingLink("http://bit.ly/bukkitcontribrunonce");
			}
			catch (Exception e) {}
		}
	}
	
	private void onBukkitContribLogin() {
		boolean update = false;
		  try {
				URL url = new URL("http://bit.ly/clientBukkitContribVersionCheck");
				HttpURLConnection con = (HttpURLConnection)(url .openConnection());
				System.setProperty("http.agent", ""); //Spoofing the user agent is required to track stats
				con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.100 Safari/534.30");
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String str;
				while ((str = in.readLine()) != null) {
				try {
					String[] split = str.split("\\.");
					int version = Integer.parseInt(split[0]) * 100 + Integer.parseInt(split[1]) * 10 + Integer.parseInt(split[2]);
					if (version > BukkitContrib.getClientVersion()){
						update = true;
						break;
					}
				}
				catch (Exception e) {}
				}
				in.close();
		  }
		  catch (Exception e) {}
		if (update) {
			BukkitContrib.createBukkitContribAlert("Update Available!", "bit.ly/bukkitcontrib", 323);
		}
	}
	
	private void onVanillaLogin() {
		pingLink("http://bit.ly/vanillalogin");
	}
	
	private void pingLink(String Url) {
		try {
			URL url = new URL(Url);
			HttpURLConnection con = (HttpURLConnection)(url.openConnection());
			System.setProperty("http.agent", ""); //Spoofing the user agent is required to track stats
			con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.100 Safari/534.30");
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String str;
			while ((str = in.readLine()) != null);
			in.close();
		}
		catch (Exception e) {}
	}
	
	public void run() {
		while(true) {
			try {
				sleep(10000);
			}
			catch (InterruptedException e1) {}
			if (onLogin) {
				doLogin();
				onLogin = false;
			}
			World world = BukkitContrib.getGameInstance().theWorld;
			if (world != null) {
				if (!runOnce) {
					multiplayer = world.multiplayerWorld;
					runOnce = true;
					onRunOnce();
					System.out.println("Ran Once, Multiplayer: " + multiplayer);
				}
				if (multiplayer != world.multiplayerWorld) {
					System.out.println("Switched to " + (world.multiplayerWorld ? "Multiplayer" : "Singleplayer"));
					if (world.multiplayerWorld) {
						doSinglePlayer();
					}
					multiplayer = world.multiplayerWorld;
				}
			}
		}
	}
}