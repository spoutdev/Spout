[![Spout][Project Logo]][Website]
What is Spout?
--------------
Spout is an open-source implementation of the [Minecraft](http://minecraft.net) server software written in Java, originally forked from Tad Hardesty's [Glowstone](https://github.com/SpaceManiac/Glowstone) project, which was originally forked from Graham Edgecombe's now-defunct [Lightstone](https://github.com/grahamedgecombe/lightstone) project.

The official server software has some shortcomings such as the use of threaded, synchronous I/O along with high CPU and RAM usage. Spout aims to be a lightweight and high-performance alternative.

Spout's goal and focus is to offer a higher performance server that implements the universal [SpoutAPI](https://github.com/SpoutDev/SpoutAPI) client/server platform. The [Vanilla](https://github.com/SpoutDev/Vanilla) plugin can be used to implement Minecraft functionality. Bukkit plugin compatibility will be possible through the [BukkitBridge](https://github.com/SpoutDev/BukkitBridge).

Copyright (c) 2011-2012, SpoutDev <http://www.spout.org>

Who is SpoutDev?
----------------
SpoutDev is the team behind the Spout and Spoutcraft projects, I See You, and Pipe.    
[![Afforess](https://secure.gravatar.com/avatar/ea0be49e1e4deac42ed9204ffd95b56c?d=mm&r=pg&s=48)](http://forums.spout.org/members/afforess.2/) [![alta189](https://secure.gravatar.com/avatar/7a087430b2bf9456b8879c5469aadb95?d=mm&r=pg&s=48)](http://forums.spout.org/members/alta189.3/) [![Wulfspider](https://secure.gravatar.com/avatar/6f2a0dcb60cd1ebee57875f9326bc98c?d=mm&r=pg&s=48)](http://forums.spout.org/members/wulfspider.1/) [![raphfrk](https://secure.gravatar.com/avatar/68186a30d5a714f6012a9c48d2b10630?d=mm&r=pg&s=48)](http://forums.spout.org/members/raphfrk.601/) [![narrowtux](https://secure.gravatar.com/avatar/f110a5b8feacea25275521f4efd0d7f2?d=mm&r=pg&s=48)](http://forums.spout.org/members/narrowtux.5/) [![Top_Cat](https://secure.gravatar.com/avatar/defeffc70d775f6df95b68f0ece46c9e?d=mm&r=pg&s=48)](http://forums.spout.org/members/top_cat.4/) [![Olloth](https://secure.gravatar.com/avatar/fa8429add105b86cf3b61dbe15638812?d=mm&r=pg&s=48)](http://forums.spout.org/members/olloth.6/) [![Rycochet](https://secure.gravatar.com/avatar/b06c12e72953e0edd3054a8645d76791?d=mm&r=pg&s=48)](http://forums.spout.org/members/rycochet.10/) [![RoyAwesome](https://secure.gravatar.com/avatar/6d258213c33a16465021daa8df299a0d?d=mm&r=pg&s=48)](http://forums.spout.org/members/royawesome.8/) [![zml2008](https://secure.gravatar.com/avatar/2320ab48d0715a4e9c73b7ec13fd6f3a?d=mm&r=pg&s=48)](http://forums.spout.org/members/zml2008.14/) [![Zidane](https://secure.gravatar.com/avatar/99532c7f117c8dac751422376116fb38?d=mm&r=pg&s=48)](http://forums.spout.org/members/zidane.7/) 

Visit our [website][Website] or get support on our [forums][Forums].  
Track and submit issues and bugs on our [issue tracker][Issues].

[![Follow us on Twitter][Twitter Logo]][Twitter][![Like us on Facebook][Facebook Logo]][Facebook][![Donate to the Spout project][Donate Logo]][Donate]

Credits
-------
 * [The Minecraft Coalition](http://wiki.vg) - protocol and file formats research.
 * [Trustin Lee](http://gleamynode.net) - author of the [Netty](http://jboss.org/netty) library.
 * Graham Edgecombe - author of the original [Lightstone](https://github.com/grahamedgecombe/lightstone) - and everyone else who has contributed to Lightstone.
 * Tad Hardesty - author of the Lightstone fork, [Glowstone](https://github.com/SpaceManiac/Glowstone) - and everyone else who has contributed to Glowstone.
 * [Notch](http://mojang.com/notch) and all the other people at [Mojang](http://mojang.com) - for making such an awesome game in the first place!

Source
------
The latest and greatest source can be found on [GitHub].  
Download the latest builds from [Jenkins].

License
-------
Spout is licensed under [GNU Lesser General Public License Version 3][License], but with a provision that files are released under the MIT license 180 days after they are published. Please see the `LICENSE.txt` file for details.

Compiling
---------
Spout uses Maven to handle its dependencies.

* Install [Maven 2 or 3](http://maven.apache.org/download.html)  
* Checkout this repo and run: `mvn clean package install`

Coding and Pull Request Formatting
----------------------------------
* Generally follow the Oracle coding standards.
* Use tabs, no spaces.
* No trailing whitespaces.
* 200 column limit for readability.
* Pull requests must compile, work, and be formatted properly.
* Sign-off on ALL your commits - this indicates you agree to the terms of our license.
* No merges should be included in pull requests unless the pull request's purpose is a merge.
* Number of commits in a pull request should be kept to *one commit* and all additional commits must be *squashed*.
* You may have more than one commit in a pull request if the commits are separate changes, otherwise squash them.
* For clarification, see the full pull request guidelines [here](http://spout.in/prguide).

**Please follow the above conventions if you want your pull request(s) accepted.**

[Project Logo]: http://cdn.spout.org/img/logo/spout_327x150.png
[License]: http://www.spout.org/SpoutDevLicenseV1.txt
[Website]: http://www.spout.org
[Forums]: http://forums.spout.org
[GitHub]: https://github.com/SpoutDev/Spout
[Jenkins]: http://build.spout.org/job/Spout
[Issues]: http://issues.spout.org
[Twitter]: http://spout.in/twitter
[Twitter Logo]: http://cdn.spout.org/img/button/twitter_follow_us.png
[Facebook]: http://spout.in/facebook
[Facebook Logo]: http://cdn.spout.org/img/button/facebook_like_us.png
[Donate]: https://www.paypal.com/cgi-bin/webscr?hosted_button_id=QNJH72R72TZ64&item_name=Spout+donation+%28from+github.com%29&cmd=_s-xclick
[Donate Logo]: http://cdn.spout.org/img/button/donate_paypal_96x96.png
