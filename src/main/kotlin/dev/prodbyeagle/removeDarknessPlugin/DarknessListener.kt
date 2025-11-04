package dev.prodbyeagle.removeDarknessPlugin

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPotionEffectEvent
import org.bukkit.event.entity.EntityPotionEffectEvent.Action
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.potion.PotionEffectType

class DarknessListener(
    private val preferences: PlayerPreferenceStore,
    private val darknessManager: DarknessManager
) : Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun handlePotionEffect(event: EntityPotionEffectEvent) {
        val player = event.entity as? Player ?: return
        val newEffect = event.newEffect ?: return
        if (newEffect.type != PotionEffectType.DARKNESS) {
            return
        }

        if (preferences.allowsDarkness(player)) {
            return
        }

        when (event.action) {
            Action.ADDED, Action.CHANGED -> event.isCancelled = true
            else -> {}
        }

        darknessManager.removeDarknessNextTick(player)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun handlePlayerJoin(event: PlayerJoinEvent) {
        darknessManager.enforcePreference(event.player)
    }
}
