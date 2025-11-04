package dev.prodbyeagle.removeDarknessPlugin

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffectType
import org.bukkit.plugin.java.JavaPlugin

class DarknessManager(
    private val plugin: JavaPlugin,
    private val preferences: PlayerPreferenceStore
) {

    fun enforcePreference(player: Player) {
        if (!preferences.allowsDarkness(player)) {
            removeDarknessNextTick(player)
        }
    }

    fun removeDarknessNextTick(player: Player) {
        if (!player.isOnline) {
            return
        }
        Bukkit.getScheduler().runTask(plugin, Runnable {
            if (player.isOnline) {
                player.removePotionEffect(PotionEffectType.DARKNESS)
            }
        })
    }
}
