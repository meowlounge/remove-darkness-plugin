package dev.prodbyeagle.removeDarknessPlugin

import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffectType

private const val DARKNESS_KEY_SUFFIX = "darkness_opt_in"

/**
 * Central service that stores player Darkness preferences and enforces the chosen behaviour.
 *
 * Preferences are persisted in each player's [org.bukkit.persistence.PersistentDataContainer] so no external config
 * management is required.
 */
class DarknessService(private val plugin: JavaPlugin) {

    private val optInKey = NamespacedKey(plugin, DARKNESS_KEY_SUFFIX)

    /**
     * Returns whether the given [player] has chosen to allow the Darkness effect.
     */
    fun allowsDarkness(player: Player): Boolean {
        val value = player.persistentDataContainer.get(optInKey, PersistentDataType.BYTE)
        return value != null && value.toInt() == 1
    }

    /**
     * Updates the Darkness preference for [player].
     *
     * @return `true` if the stored preference changed, `false` if it already matched [allow].
     */
    fun setAllowsDarkness(player: Player, allow: Boolean): Boolean {
        val current = allowsDarkness(player)
        if (current == allow) {
            return false
        }

        val container = player.persistentDataContainer
        if (allow) {
            container.set(optInKey, PersistentDataType.BYTE, 1.toByte())
        } else {
            container.remove(optInKey)
        }

        if (!allow) {
            applyPreference(player)
        }

        return true
    }

    /**
     * Toggles the Darkness preference for [player] and returns the new setting.
     */
    fun toggle(player: Player): Boolean {
        val allow = !allowsDarkness(player)
        setAllowsDarkness(player, allow)
        return allow
    }

    /**
     * Applies the stored preference for [player], removing Darkness if they have opted out.
     */
    fun applyPreference(player: Player) {
        if (!allowsDarkness(player)) {
            removeDarknessNextTick(player)
        }
    }

    /**
     * Schedules Darkness removal for the next tick to ensure the potion effect is cleared safely.
     */
    private fun removeDarknessNextTick(player: Player) {
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
