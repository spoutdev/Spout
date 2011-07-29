[![][Project Logo]][Website]
Spout
=====
Spout is a plugin for Bukkit-based servers and a Minecraft client mod system that exposes new API for other plugins to utilize, in an attempt to bring the Minecraft Multiplayer experience to parity with the single player modding experience.


Copyright &copy; 2011, Afforess <afforess@gmail.com>  
Spout is licensed under [GNU LESSER GENERAL PUBLIC LICENSE Version 3][License]

Visit our [website][Website].  
Get support on our [Bukkit forum thread][Forum].  
Track and submit issues and bugs on our [issue tracker][Issues].

Follow Spout on Twitter [@Afforess][Twitter]

Source
------
The latest and greatest source of Spout can be found on [GitHub].  
Download the latest builds from [Jenkins].  
View the latest [Javadoc].

Compiling
---------
Spout uses Maven to handle it's dependencies.

Spout requires CraftBukkit (preferrably the latest version or source).  
* Install [Maven 2 or 3](http://maven.apache.org/download.html)  
* Checkout this repo and run: `mvn clean package`

Spout SP requires the Minecraft Coder Pack.  
* Download and extract the latest version of [Minecraft Coder Pack][MCP].  
* Copy the latest complete Minecraft bin directory from your computer.  
* Place the Minecraft bin directory under MCP's `jars` directory.  
* Checkout or copy the latest source fro the Spout SP repo.  
* Checkout or copy the conf directory of files from the Spout SP repo to MCP's `conf` directory.  
* Run cleanup, decompile, recompile, and reobfuscate (.bat for Windows or .sh for Linux).  
* When tasks are finished, the compiled Spout class files will be located in reobf.

Coding and Pull Request Formatting
----------------------------------
* Generally follows the Oracle coding standards.
* Spout uses tabs, no spaces.
* No 80 column limit or midstatement newlines.
* Pull requests must compile and work.
* Pull requests must be formatted properly.
* When modifying Notch code (Minecraft vanilla code), include `//Spout Start`and `//Spout end`

Please follow the above conventions if you want your pull requests accepted.

[Project Logo]: http://assets.craftfire.com/img/logo/spout.png
[License]: http://www.gnu.org/licenses/lgpl.html
[Website]: http://getspout.org
[Forum]: http://bit.ly/getspout
[GitHub]: https://github.com/Afforess/Spout
[Javadoc]: http://ci.craftfire.com/view/Afforess/job/Spout/javadoc
[Jenkins]: http://ci.craftfire.com/view/Afforess
[MCP]: http://mcp.ocean-labs.de/index.php/MCP_Releases
[Issues]: https://github.com/Afforess/Spout/issues
[Twitter]: http://twitter.com/Afforess
