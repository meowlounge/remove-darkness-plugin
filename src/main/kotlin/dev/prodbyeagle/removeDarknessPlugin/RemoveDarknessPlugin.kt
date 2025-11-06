package dev.prodbyeagle.removeDarknessPlugin

import org.bukkit.plugin.java.JavaPlugin

class RemoveDarknessPlugin : JavaPlugin() {

    private lateinit var darknessService: DarknessService

    /**
     * Constructs the service, registers listeners and commands, and enforces preferences for
     * players already online when the plugin loads.
     */
    override fun onEnable() {
        darknessService = DarknessService(this)

        server.pluginManager.registerEvents(
            DarknessListener(darknessService),
            this
        )

        val command = DarknessCommand(darknessService)
        getCommand("darkness")?.apply {
            setExecutor(command)
            tabCompleter = command
        } ?: logger.severe("Command 'darkness' is missing from plugin.yml.")

        server.onlinePlayers.forEach(darknessService::applyPreference)
    }
}
