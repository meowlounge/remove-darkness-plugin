package dev.prodbyeagle.removeDarknessPlugin

import java.util.Locale
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class DarknessCommand(
    private val preferences: PlayerPreferenceStore,
    private val darknessManager: DarknessManager
) : TabExecutor {

    private val enableKeywords = setOf("on", "enable", "true", "allow")
    private val disableKeywords = setOf("off", "disable", "false", "deny")

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        val player = sender as? Player
        if (player == null) {
            sender.sendMessage("Only players can use /$label.")
            return true
        }

        if (args.isEmpty()) {
            val newPreference = preferences.toggle(player)
            notifyPreference(player, newPreference, changed = true)
            darknessManager.enforcePreference(player)
            return true
        }

        val keyword = args[0].lowercase(Locale.ROOT)
        return when (keyword) {
            "status" -> {
                player.sendMessage(
                    if (preferences.allowsDarkness(player)) {
                        "Darkness effects are currently enabled for you."
                    } else {
                        "Darkness effects are currently blocked for you."
                    }
                )
                true
            }
            in enableKeywords -> {
                val changed = preferences.setAllowsDarkness(player, true)
                notifyPreference(player, allow = true, changed = changed)
                true
            }
            in disableKeywords -> {
                val changed = preferences.setAllowsDarkness(player, false)
                notifyPreference(player, allow = false, changed = changed)
                darknessManager.enforcePreference(player)
                true
            }
            "toggle" -> {
                val newPreference = preferences.toggle(player)
                notifyPreference(player, newPreference, changed = true)
                darknessManager.enforcePreference(player)
                true
            }
            else -> {
                player.sendMessage("Usage: /$label [on|off|toggle|status]")
                true
            }
        }
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String> {
        if (args.size > 1) {
            return mutableListOf()
        }

        val options = sequenceOf("on", "off", "toggle", "status")
        val current = args.firstOrNull()?.lowercase(Locale.ROOT).orEmpty()
        return options.filter { it.startsWith(current) }.toMutableList()
    }

    private fun notifyPreference(player: Player, allow: Boolean, changed: Boolean) {
        val message = if (allow) {
            if (changed) {
                "You will now experience darkness effects."
            } else {
                "Darkness effects are already enabled for you."
            }
        } else {
            if (changed) {
                "Darkness effects will now be removed for you."
            } else {
                "Darkness effects are already blocked for you."
            }
        }
        player.sendMessage(message)
    }
}
