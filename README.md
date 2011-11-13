[![][Project Logo]][Website]
Spout
=====
Spout is a plugin for Bukkit-based servers and a Minecraft client mod system that exposes new API for other plugins to utilize, in an attempt to bring the Minecraft Multiplayer experience to parity with the single player modding experience.

Copyright &copy; 2011, SpoutDev <dev@getspout.org>  
Spout is licensed under [GNU LESSER GENERAL PUBLIC LICENSE Version 3][License]

Visit our [website][Website].  
Get support on our [Bukkit forum thread][Forum].  
Track and submit issues and bugs on our [issue tracker][Issues].

[![][Twitter Logo]][Twitter][![][Facebook Logo]][Facebook][![][Donate Logo]][Donate]

Source
------
The latest and greatest source can be found on [GitHub].  
Download the latest builds from [Jenkins].  
View the latest [Javadoc].

Compiling
---------
Spout uses Maven to handle its dependencies.

Spout requires SpoutAPI and Bukkit (preferrably the latest versions or source).  
* Install [Maven 2 or 3](http://maven.apache.org/download.html)  
* Checkout this repo and run: `mvn clean package`

Coding and Pull Request Formatting
----------------------------------
* Generally follow the Oracle coding standards.
* Use tabs, no spaces.
* No 80 column limit or midstatement newlines.
* Pull requests must compile and work.
* Pull requests must be formatted properly.
* If you change a packet or widget's read/write/number of bytes, be sure to increment the version on both the server and client.

**Please follow the above conventions if you want your pull request(s) accepted.**

[Project Logo]: http://assets.craftfire.com/img/logo/spout_327x150.png
[License]: http://www.gnu.org/licenses/lgpl.html
[Website]: http://www.getspout.org
[Forum]: http://spout.in/bukkit
[GitHub]: https://github.com/SpoutDev/Spout
[Javadoc]: http://spout.in/jddev
[Jenkins]: http://spout.in/ci
[Issues]: http://spout.in/issues
[Twitter]: http://spout.in/twitter
[Twitter Logo]: http://cdn.getspout.org/img/button/twitter_follow_us.png
[Facebook]: http://spout.in/facebook
[Facebook Logo]: http://cdn.getspout.org/img/button/facebook_like_us.png
[Donate]: https://www.paypal.com/cgi-bin/webscr?hosted_button_id=QNJH72R72TZ64&item_name=Spoutcraft+%28from+GitHub.com%29&cmd=_s-xclick
[Donate Logo]: http://cdn.getspout.org/img/button/donate_paypal_96x96.png
[MCP]: http://mcp.ocean-labs.de/index.php/MCP_Releases
