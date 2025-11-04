package dev.prodbyeagle.removeDarknessPlugin

import org.bukkit.plugin.java.JavaPlugin

class RemoveDarknessPlugin : JavaPlugin() {

    private lateinit var preferenceStore: PlayerPreferenceStore
    private lateinit var darknessManager: DarknessManager

    override fun onEnable() {
        reloadConfig()

        preferenceStore = PlayerPreferenceStore(this).apply { reload() }
        darknessManager = DarknessManager(this, preferenceStore)

        server.pluginManager.registerEvents(
            DarknessListener(preferenceStore, darknessManager),
            this
        )

        val command = DarknessCommand(preferenceStore, darknessManager)
        getCommand("darkness")?.apply {
            setExecutor(command)
            tabCompleter = command
        } ?: logger.severe("Command 'darkness' is missing from plugin.yml.")

        server.onlinePlayers.forEach(darknessManager::enforcePreference)
    }

    override fun onDisable() {
        if (::preferenceStore.isInitialized) {
            preferenceStore.save()
        }
    }
}
