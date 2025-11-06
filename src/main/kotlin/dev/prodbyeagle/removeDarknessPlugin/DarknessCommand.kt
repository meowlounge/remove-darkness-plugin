package dev.prodbyeagle.removeDarknessPlugin

import java.util.Locale
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

/**
 * Handles the `/darkness` command so players can manage their Darkness preference.
 */
class DarknessCommand(
    private val darknessService: DarknessService
) : TabExecutor {

    /**
     * Supported subcommands together with their recognised string aliases.
     */
    private enum class SubCommand(val primaryAlias: String, vararg aliases: String) {
        STATUS("status", "info"),
        ALLOW("allow", "enable", "on", "true"),
        DENY("deny", "disable", "off", "false"),
        TOGGLE("toggle");

        val allAliases: Set<String> = setOf(primaryAlias, *aliases)

        companion object {
            private val lookup = entries.flatMap { command ->
                command.allAliases.map { alias -> alias to command }
            }.toMap()

            val tabOptions: List<String> = entries.map(SubCommand::primaryAlias)

            fun from(token: String): SubCommand? = lookup[token]
        }
    }

    /**
     * Executes the command, toggling or reporting the caller's current Darkness setting.
     */
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
            val newPreference = darknessService.toggle(player)
            notifyPreference(player, newPreference, changed = true)
            return true
        }

        val keyword = args[0].lowercase(Locale.ROOT)
        return when (SubCommand.from(keyword)) {
            SubCommand.STATUS -> {
                player.sendMessage(
                    if (darknessService.allowsDarkness(player)) {
                        "Darkness effects are currently enabled for you."
                    } else {
                        "Darkness effects are currently blocked for you."
                    }
                )
                true
            }
            SubCommand.ALLOW -> {
                val changed = darknessService.setAllowsDarkness(player, allow = true)
                notifyPreference(player, allow = true, changed = changed)
                true
            }
            SubCommand.DENY -> {
                val changed = darknessService.setAllowsDarkness(player, allow = false)
                notifyPreference(player, allow = false, changed = changed)
                true
            }
            SubCommand.TOGGLE -> {
                val newPreference = darknessService.toggle(player)
                notifyPreference(player, newPreference, changed = true)
                true
            }
            null -> {
                player.sendMessage("Usage: /$label [allow|deny|toggle|status]")
                true
            }
        }
    }

    /**
     * Suggests valid subcommands for the first argument.
     */
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String> {
        if (args.size > 1) {
            return mutableListOf()
        }

        val current = args.firstOrNull()?.lowercase(Locale.ROOT).orEmpty()
        return SubCommand.tabOptions
            .filter { it.startsWith(current) }
            .toMutableList()
    }

    /**
     * Sends feedback to [player] explaining whether Darkness is now enabled or disabled.
     */
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
