package dev.prodbyeagle.removeDarknessPlugin

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPotionEffectEvent
import org.bukkit.event.entity.EntityPotionEffectEvent.Action
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.potion.PotionEffectType

/**
 * Bukkit listener that suppresses Darkness potion effects for players who have not opted in.
 */
class DarknessListener(
    private val darknessService: DarknessService
) : Listener {

    /**
     * Cancels Darkness potion applications for players who prefer to keep the effect disabled.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun handlePotionEffect(event: EntityPotionEffectEvent) {
        val player = event.entity as? Player ?: return
        val newEffect = event.newEffect ?: return
        if (newEffect.type != PotionEffectType.DARKNESS) {
            return
        }

        if (darknessService.allowsDarkness(player)) {
            return
        }

        when (event.action) {
            Action.ADDED, Action.CHANGED -> event.isCancelled = true
            else -> {}
        }

        darknessService.applyPreference(player)
    }

    /**
     * Re-applies a joining player's stored preference to ensure Darkness stays disabled if needed.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    fun handlePlayerJoin(event: PlayerJoinEvent) {
        darknessService.applyPreference(event.player)
    }
}
