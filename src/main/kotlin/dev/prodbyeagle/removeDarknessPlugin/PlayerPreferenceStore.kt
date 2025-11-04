package dev.prodbyeagle.removeDarknessPlugin

import java.util.UUID
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

private const val CONFIG_KEY = "players-allowing-darkness"

class PlayerPreferenceStore(private val plugin: JavaPlugin) {

    private val playersAllowingDarkness = mutableSetOf<UUID>()

    fun reload() {
        playersAllowingDarkness.clear()
        plugin.config.getStringList(CONFIG_KEY)
            .mapNotNull { runCatching { UUID.fromString(it) }.getOrNull() }
            .forEach(playersAllowingDarkness::add)
    }

    fun save() {
        plugin.config.set(CONFIG_KEY, playersAllowingDarkness.map(UUID::toString))
        plugin.saveConfig()
    }

    fun allowsDarkness(uniqueId: UUID): Boolean = playersAllowingDarkness.contains(uniqueId)

    fun allowsDarkness(player: Player): Boolean = allowsDarkness(player.uniqueId)

    fun setAllowsDarkness(uniqueId: UUID, allow: Boolean): Boolean {
        val changed = if (allow) {
            playersAllowingDarkness.add(uniqueId)
        } else {
            playersAllowingDarkness.remove(uniqueId)
        }

        if (changed) {
            save()
        }

        return changed
    }

    fun setAllowsDarkness(player: Player, allow: Boolean): Boolean =
        setAllowsDarkness(player.uniqueId, allow)

    fun toggle(player: Player): Boolean {
        val newPreference = !allowsDarkness(player)
        setAllowsDarkness(player, newPreference)
        return newPreference
    }
}
