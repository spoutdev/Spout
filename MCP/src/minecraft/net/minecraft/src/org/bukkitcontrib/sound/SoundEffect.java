package org.getspout.Spout.sound;

import java.util.HashMap;
import java.util.Map;

public enum SoundEffect {
	 /* Ambient Sound Effects */
	 CAVE_MYSTERIOUS(0, "ambient.cave.cave", 0),
	 CAVE_LIGHT_MYSTERIOUS(1, "ambient.cave.cave", 1),
	 CAVE_SHADOW(2, "ambient.cave.cave", 2),
	 CAVE_DEEP(3, "ambient.cave.cave", 3),
	 CAVE_NEW_PASSAGE(4, "ambient.cave.cave", 4),
	 CAVE_PASSING_SHADOW(5, "ambient.cave.cave", 5),
	 CAVE_DARK_SHADOW(6, "ambient.cave.cave", 6),
	 CAVE_FEAR(7, "ambient.cave.cave", 7),
	 CAVE_DARK_MYSTERIOUS(8, "ambient.cave.cave", 8),
	 CAVE_LARGE_FEAR(9, "ambient.cave.cave", 9),
	 CAVE_RUMBLE(10, "ambient.cave.cave", 10),
	 CAVE_SHORT_MYSTERIOUS(11, "ambient.cave.cave", 11),
	 CAVE_MONSTER_ROAR(12, "ambient.cave.cave", 12),
	 WEATHER_RAIN_1(13, "ambient.weather.rain", 0),
	 WEATHER_RAIN_2(14, "ambient.weather.rain", 1),
	 WEATHER_RAIN_3(15, "ambient.weather.rain", 2),
	 WEATHER_RAIN_4(16, "ambient.weather.rain", 3),
	 WEATHER_THUNDER_1(17, "ambient.weather.thunder", 0),
	 WEATHER_THUNDER_2(18, "ambient.weather.thunder", 1),
	 WEATHER_THUNDER_3(19, "ambient.weather.thunder", 2),
	 FALL_1(20, "damage.fallbig", 0),
	 FALL_2(21, "damage.fallbig", 1),
	 FALL_3(22, "damage.fallsmall"),
	 HURT_1(23, "damage.hurtflesh", 0),
	 HURT_2(24, "damage.hurtflesh", 1),
	 HURT_3(25, "damage.hurtflesh", 2),
	 FIRE(26, "fire.fire"),
	 FIRE_IGNITE(27, "fire.ignite"),
	 LAVA(28, "liquid.lava"),
	 LAVA_POP(29, "liquid.lavapop"),
	 WATER_SPLASH(30, "liquid.splash"),
	 WATER(31, "liquid.water"),
	 
	 /* Mob Sound Effects */
	 CHICKEN(32, "mob.chicken"),
	 CHICKEN_HURT(33, "mob.chickenhurt"),
	 COW(34, "mob.cow"),
	 COW_HURT(35, "mob.cowhurt"),
	 CREEPER(36, "mob.creeper"),
	 CREEPER_HURT(37, "mob.creeperdeath"),
	 PIG(38, "mob.pig"),
	 PIG_HURT(39, "mob.pigdeath"),
	 SHEEP(40, "mob.sheep"),
	 SKELETON(42, "mob.skeleton"),
	 SKELETON_HURT(43, "mob.skeletonhurt"),
	 SLIME(44, "mob.slime"),
	 SLIME_ATTACK(45, "mob.slimeattack"),
	 SPIDER(46, "mob.spider"),
	 SPIDER_HURT(47, "mob.spiderdeath"),
	 ZOMBIE(48, "mob.zombie"),
	 ZOMBIE_HURT(49, "mob.zombiehurt"),
	 GHAST_MOAN(50, "mob.ghast.moan"),
	 GHAST_SCREAM(51, "mob.ghast.scream"),
	 GHAST_CHARGE(52, "mob.ghast.charge"),
	 GHAST_DEATH(53, "mob.ghast.death"),
	 WOLF_BARK(54, "mob.wolf.bark"),
	 WOLF_GROWL(55, "mob.wolf.growl"),
	 WOLF_HOWL(56, "mob.wolf.howl"),
	 WOLF_HURT(57, "mob.wolf.hurt"),
	 WOLF_PANTING(58, "mob.wolf.panting"),
	 WOLF_WHINE(59, "mob.wolf.whine"),
	 WOLF_SHAKE(60, "mob.wolf.shake"),
	 WOLF_DEATH(61, "mob.wolf.death"),
	 ZOMBIEPIG(62, "mob.zombiepig.zpig"),
	 ZOMBIEPIG_HURT(63, "mob.zombiepig.zpighurt"),
	 ZOMBIEPIG_ANGRY(64, "mob.zombiepig.zpigangry"),
	 
	 /* Block Sound Effects */
	 PORTAL(65, "portal.portal"),
	 PORTAL_TRAVEL(66, "portal.travel"),
	 PROTAL_TRIGGER(67, "portal.trigger"),
	 CLOTH(68, "step.cloth"),
	 GRASS(69, "step.grass"),
	 GRAVEL(70, "step.gravel"),
	 SAND(71, "step.sand"),
	 SNOW(72, "step.snow"),
	 STONE(73, "step.stone"),
	 WOOD(74, "step.wood"),
	 
	 /* Random Sound Effects */
	 BOW(75, "random.bow"),
	 BREATH(76, "random.breath"),
	 CLICK(77, "random.click"),
	 DOOR_CLOSE(78, "random.door_close"),
	 DOOR_OPEN(79, "random.door_open"),
	 BOW_STRING(80, "random.drr"),
	 EXPLODE(81, "random.explode"),
	 FIZZ(82, "random.fizz"),
	 FUSE(83, "random.fuse"),
	 GLASS_BREAK_1(84, "random.glass", 0),
	 GLASS_BREAK_2(85, "random.glass", 1),
	 GLASS_BREAK_3(86, "random.glass", 2),
	 HURT(87, "random.hurt"),
	 POP(88, "random.pop"),
	 SPLASH(89, "random.splash"),
	 WOOD_CLICK(90, "random.wood click"),	 
	 ;
	 
	 private final int id;
	 private final String name;
	 private final int soundId;
	 private static final Map<String, SoundEffect> lookupName = new HashMap<String, SoundEffect>();
	 private static final Map<Integer, SoundEffect> lookupId = new HashMap<Integer, SoundEffect>();
	 private static int last = 0;
	 SoundEffect(final int id, final String name) {
		  this.id = id;
		  this.name = name;
		  this.soundId = -1;
	 }
	 
	 SoundEffect(final int id, final String name, final int soundId) {
		  this.id = id;
		  this.name = name;
		  this.soundId = soundId;
	 }
	 
	 public int getId() {
		  return id;
	 }
	 
	 public String getName() {
		  return name;
	 }
	 
	 public int getSoundId() {
		  return soundId;
	 }
	 
	 public static SoundEffect getSoundEffectFromId(int id) {
		  return lookupId.get(id);
	 }
	 
	 public static SoundEffect getSoundEffectFromName(String name) {
		  return lookupName.get(name);
	 }
	 
	 public static int getMaxId() {
		  return last;
	 }
	 
	 static {
		  for (SoundEffect i : values()) {
				lookupName.put(i.getName(), i);
				lookupId.put(i.getId(), i);
				if (i.getId() > last) {
					 last = i.getId();
				}
		  }
	 }

}
