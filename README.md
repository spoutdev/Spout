[![][Project Logo]][Website]
Spout
=====
Spout is a plugin for Bukkit-based servers and a Minecraft client mod system that exposes new API for other plugins to utilize, in an attempt to bring the Minecraft Multiplayer experience to parity with the single player modding experience.

Copyright &copy; 2011, SpoutDev <dev@getspout.org>  
Spout is licensed under [GNU LESSER GENERAL PUBLIC LICENSE Version 3][License]

Visit our [website][Website].  
Get support on our [Bukkit forum thread][Forum].  
Track and submit issues and bugs on our [issue tracker][Issues].

Follow Spout on Twitter [@SpoutDev][Twitter]

Source
------
The latest and greatest source of Spout can be found on [GitHub].  
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
* Generally follows the Oracle coding standards.
* Spout uses tabs, no spaces.
* No 80 column limit or midstatement newlines.
* Pull requests must compile and work.
* Pull requests must be formatted properly.
* When modifying Notch code (Minecraft vanilla code), include `//Spout start`and `//Spout end`

Please follow the above conventions if you want your pull requests accepted.

[Project Logo]: http://assets.craftfire.com/img/logo/spout_327x150.png
[License]: http://www.gnu.org/licenses/lgpl.html
[Website]: http://www.getspout.org
[Forum]: http://bit.ly/getspout
[GitHub]: https://github.com/SpoutDev/Spout
[Javadoc]: http://jddev.getspout.org
[Jenkins]: http://ci.getspout.org/view/SpoutDev
[Issues]: https://github.com/SpoutDev/Spout/issues
[Twitter]: http://twitter.com/SpoutDev
[MCP]: http://mcp.ocean-labs.de/index.php/MCP_Releases
