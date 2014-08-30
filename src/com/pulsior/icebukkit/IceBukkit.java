package com.pulsior.icebukkit;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.pulsior.icebukkit.ParticleEffect.ParticleEffectPacket;

public final class IceBukkit extends JavaPlugin implements Listener
{

	ItemStack bucket = new ItemStack(Material.WATER_BUCKET);

	@Override
	public void onEnable()
	{

		ParticleEffectPacket.initialize();

		Bukkit.getPluginManager().registerEvents(this, this);
		ItemMeta meta = bucket.getItemMeta();
		meta.setDisplayName(ChatColor.RESET + "Ice Water Bucket");

		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.GOLD +
				"Right-click to pour ice water over yourself!");
		meta.setLore(lore);

		bucket.setItemMeta(meta);

		ShapedRecipe recipe = new ShapedRecipe(bucket);
		recipe.shape("XXX", "XYX", "XXX");
		recipe.setIngredient('X', Material.SNOW_BALL);
		recipe.setIngredient('Y', Material.WATER_BUCKET);
		getServer().addRecipe(recipe);

	}

	@Override
	public void onDisable()
	{

	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event)
	{
		Player p = event.getPlayer();
		Action a = event.getAction();
		if (a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK)
		{
			ItemStack heldItem = event.getItem();
			if (heldItem != null)
			{
				if (heldItem.equals(bucket))
				{
					if (p.hasPermission(new Permission("icebukkit.use")))
					{
						event.setCancelled(true);

						Location location = p.getLocation();
						location.setY(location.getY() + 2);
						final Block block = p.getWorld().getBlockAt(location);

						if (block.getType() == Material.AIR)
						{
							block.setType(Material.WATER);
							heldItem.setType(Material.BUCKET);
							sendParticles(location);

							BukkitRunnable task = new BukkitRunnable() {
								@Override
								public void run()
								{
									block.setType(Material.AIR);
								}
							};
							Bukkit.getScheduler()
									.scheduleSyncDelayedTask(this, task, 10L);

							p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 3));
							p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 3));

						}
					}

					else
					{
						p.sendMessage(ChatColor.RED +
								"You don't have permission for that!");
					}
				}
				
			}
			else
			{
				event.getPlayer().getInventory().addItem(bucket);
				
			}
		}
	}

	public void sendParticles(Location center)
	{
		Bukkit.getLogger().info("Rendered!");
		ParticleEffectPacket p = new ParticleEffectPacket("fireworksSpark", 0.5F, 0.25F, 0.5F, 0, 1000);
		p.sendTo(center, 50);
	}
}
