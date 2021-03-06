package net.eduard.api

import net.eduard.api.command.*
import net.eduard.api.command.api.ApiCommand
import net.eduard.api.command.map.MapCommand
import net.eduard.api.core.BukkitReplacers
import net.eduard.api.core.BukkitInfoGenerator
import net.eduard.api.core.PlayerSkin
import net.eduard.api.hooks.JHCashHook
import net.eduard.api.hooks.StoryHook
import net.eduard.api.lib.abstraction.EntidadesNMSTeste
import net.eduard.api.lib.modules.BukkitBungeeAPI
import net.eduard.api.lib.bungee.BungeeAPI
import net.eduard.api.lib.modules.ServerAPI.BukkitControl
import net.eduard.api.lib.config.Config
import net.eduard.api.lib.database.DBManager
import net.eduard.api.lib.game.SoundEffect
import net.eduard.api.lib.manager.CommandManager
import net.eduard.api.lib.menu.Menu
import net.eduard.api.lib.modules.Copyable.CopyDebug
import net.eduard.api.lib.modules.Extra
import net.eduard.api.lib.modules.Mine
import net.eduard.api.lib.modules.MineReflect
import net.eduard.api.lib.modules.VaultAPI
import net.eduard.api.lib.storage.StorageAPI
import net.eduard.api.lib.storage.bukkit_storables.BukkitStorables
import net.eduard.api.lib.game.Schematic
import net.eduard.api.lib.kotlin.examples.EventsAlterations
import net.eduard.api.lib.plugin.HybridPlugin
import net.eduard.api.lib.plugin.IPluginInstance
import net.eduard.api.listener.*
import net.eduard.api.server.currency.CurrencyController
import net.eduard.api.server.currency.list.CurrencyVaultEconomy
import net.eduard.api.server.minigame.Minigame
import net.eduard.api.task.AutoSaveAndBackupTask
import net.eduard.api.task.PlayerTargetPlayerTask
import net.eduard.api.task.BuilderTask
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

/**
 * Classe Principal do EduardAPI
 *
 * @author Eduard
 * @version 1.3
 * @since 0.5
 */
class EduardAPI(plugin: IPluginInstance) : HybridPlugin() {
    init {
        pluginBase = plugin
    }

    override fun getPlugin(): JavaPlugin {
        return pluginBase.plugin as JavaPlugin
    }

    override fun console(message: String) {
        Bukkit.getConsoleSender().sendMessage(message)
    }

    override fun onEnable() {
        instance = this
        isFree = true
        StorageAPI.setDebug(configs.getBoolean("debug-storage"))
        log("Registrando classes da EduardLIB")
        StorageAPI.registerPackage(javaClass, "net.eduard.api.lib")
        BukkitStorables.load()
        StorageAPI.startGson()
        log("Storables do Bukkit carregado!")
        MAPS_CONFIG = Config(this, "maps/")
        VaultAPI.setupVault()
        BukkitControl.register(plugin)
        BukkitBungeeAPI.requestCurrentServer()
        BungeeAPI.getBukkit().plugin = plugin
        BungeeAPI.getBukkit().register()
        StorageAPI.setDebug(configs.getBoolean("debug-storage"))
        StorageAPI.registerPackage(Minigame::class.java)
        reload()
        Mine.resetScoreboards()
        log("Scoreboards dos jogadores online resetadas!")
        log("Ativando tasks (Timers)")
        // Na versão 1.16 precisa ser em Sync não pode ser Async
        PlayerTargetPlayerTask().runTaskTimerAsynchronously(plugin, 20, 20)
        AutoSaveAndBackupTask().runTaskTimerAsynchronously(plugin, 20, 20)
        BuilderTask().asyncTimer()
        log("Ativando comandos")
        ApiCommand().register()
        MapCommand().register()
        EnchantCommand().register()
        GotoCommand().register()
        SoundCommand().register()
        SetXPCommand().register()
        SetSkinCommand().register()
        log("Comandos ativados com sucesso")
        log("Ativando listeners dos Eventos")
        EduardAPIEvents().register(this)
        SupportActivations().register(this)
        EduWorldEditListener().register(this)
        PlayerTargetListener().register(this)
        log("Listeners dos Eventos ativados com sucesso")
        log("Gerando Base de dados de Enums do Bukkit")
        BukkitInfoGenerator(this)
        log("Ativando replacers")
        BukkitReplacers()
        log("Carregado com sucesso!")
        CurrencyController.getInstance().register(CurrencyVaultEconomy())
        log("Carregando dependencias")
        JHCashHook()
        StoryHook()
        testingKotlin()
    }

    private fun testingKotlin() {
        log("Testando Kotlin")
        EntidadesNMSTeste().register(this)
        EventsAlterations()
    }


    override fun reload() {
        log("Inicio do Recarregamento do EduardAPI")
        configs.reloadConfig()
        messages.reloadConfig()
        log("Ativando debug de sistemas caso marcado na config como 'true'")
        StorageAPI.setDebug(configs.getBoolean("debug-storage"))
        DBManager.setDebug(configs.getBoolean("debug-db"))
        Menu.isDebug = configs.getBoolean("debug-menu")
        CommandManager.isDebug = configs.getBoolean("debug-commands")
        CopyDebug.setDebug(configs.getBoolean("debug-copyable"))
        BukkitBungeeAPI.setDebug(configs.getBoolean("debug-bungee-bukkit"))
        Mine.OPT_DEBUG_REPLACERS = configs.getBoolean("debug-replacers")
        PlayerSkin.reloadSkins()
        MineReflect.MSG_ITEM_STACK = configs.message("stack-design")
        loadMaps()
        log("Mapas carregados!")
        configs.add("sound-teleport", OPT_SOUND_TELEPORT)
        configs.add("sound-error", OPT_SOUND_ERROR)
        configs.add("sound-success", OPT_SOUND_SUCCESS)
        configs.saveConfig()
        Mine.OPT_AUTO_RESPAWN = configs.getBoolean("auto-respawn")
        Mine.OPT_NO_JOIN_MESSAGE = configs.getBoolean("no-join-message")
        Mine.OPT_NO_QUIT_MESSAGE = configs.getBoolean("no-quit-message")
        Mine.OPT_NO_DEATH_MESSAGE = configs.getBoolean("no-death-message")
        try {
            log("Carregando formato de dinheiro da config")
            Extra.MONEY = DecimalFormat(configs.getString("money-format"),
                    DecimalFormatSymbols.getInstance(Locale.forLanguageTag(configs.getString("money-format-locale"))))
            log("Formato valido")
        } catch (e: Exception) {
            error("Formato do dinheiro invalido " + configs.getString("money-format"))
        }
        Mine.MSG_ON_JOIN = configs.message("on-join-message")
        Mine.MSG_ON_QUIT = configs.message("on-quit-message")
        OPT_SOUND_TELEPORT = configs.getSound("sound-teleport")
        OPT_SOUND_ERROR = configs.getSound("sound-error")
        OPT_SOUND_SUCCESS = configs.getSound("sound-success")
    }

    override val pluginName: String
        get() = plugin.name


    override fun onDisable() {
        PlayerSkin.saveSkins()
        saveMaps()
        log("Mapas salvados!")
        log("desativado com sucesso!")
        BungeeAPI.getController().unregister()


    }

    companion object {

        lateinit var instance: EduardAPI
            private set
            @JvmStatic
            get


        /**
         * Som para o Teleporte
         */
        var OPT_SOUND_TELEPORT = SoundEffect.create("ENDERMAN_TELEPORT")

        /**
         * Som para algum sucesso
         */
        var OPT_SOUND_SUCCESS = SoundEffect.create("LEVEL_UP")

        /**
         * Som para algum erro
         */
        var OPT_SOUND_ERROR = SoundEffect.create("NOTE_BASS_DRUM")


        /*

        Som do rosnar do gato
        private val ROSNAR = SoundEffect.create("CAT_PURR")

        private val VALUE_TNT_POWER = 4f

        private val VALUE_CREEPER_POWER = 3f

        private val VALUE_WALKING_VELOCITY = -0.08f

        private val DAY_IN_HOUR = 24

        private val DAY_IN_MINUTES = DAY_IN_HOUR * 60

        private val DAY_IN_SECONDS = DAY_IN_MINUTES * 60

        private val DAY_IN_TICKS = (DAY_IN_SECONDS * 20).toLong()

        private val DAY_IN_MILLIS = DAY_IN_TICKS * 50

         */

        fun getSchematic(player: Player): Schematic {
            var schema = MAPS_CACHE[player]
            if (schema == null) {
                schema = Schematic()
                MAPS_CACHE[player] = schema
            }
            return schema
        }

        fun loadMaps() {

            val file = MAPS_CONFIG.file
            file.mkdirs()

            if (file.listFiles() == null)
                return

            for (subfile in file.listFiles()!!) {
                if (!subfile.isDirectory) {
                    MAPS[subfile.name.replace(".map", "")] = Schematic.load(subfile)
                }
            }

        }

        /*
        * Mapa de Arenas registradas
        */
        var MAPS = mutableMapOf<String, Schematic>()
        var MAPS_CACHE: MutableMap<Player, Schematic> = HashMap()

        lateinit var MAPS_CONFIG: Config

        /**
         * Salva todos os mapas no sistema de armazenamento
         */
        fun saveMaps() {
            MAPS_CONFIG.file.mkdirs()
            for ((name, mapa) in MAPS) {

                mapa.save(File(MAPS_CONFIG.file, "$name.map"))
            }
        }
    }

}
