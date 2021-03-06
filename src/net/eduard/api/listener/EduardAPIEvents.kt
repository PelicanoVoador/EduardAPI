package net.eduard.api.listener

import net.eduard.api.EduardAPI
import net.eduard.api.lib.manager.EventsManager
import net.eduard.api.lib.modules.Mine
import net.eduard.api.lib.modules.MineReflect
import net.eduard.api.core.PlayerSkin
import net.eduard.api.server.EduardPlugin
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.server.ServerListPingEvent
import org.bukkit.scheduler.BukkitRunnable

/**
 * Pequenas manipulações de Eventos criados que qualquer servidor precise
 *
 * @since 2.3
 * @version 1.0
 *
 * @author Eduard
 */
class EduardAPIEvents : EventsManager() {



    @EventHandler
    fun onChat(e : AsyncPlayerChatEvent){
        val p = e.player
        if (p.hasPermission("chat.color")){
            e.message = ChatColor.translateAlternateColorCodes('&',e.message)
        }
    }

    @EventHandler
    fun marketing(e: PlayerJoinEvent) {
        val p = e.player
        if (EduardAPI.instance.getBoolean("skins")) {
            PlayerSkin.change(p, p.name)
        }
        if (p.hasPermission("eduard.plugins")) {
            for (plugin in Bukkit.getPluginManager().plugins) {
                if (plugin is EduardPlugin) {
                    if (plugin.isEnabled()) {
                        p.sendMessage("§b[Eduard-Dev] §f" + plugin.getName() + " §fv"
                                + plugin.getDescription().version + "§a esta ativado.")
                    } else {
                        p.sendMessage("§b[Eduard-Dev] §f" + plugin.getName() + " §fv"
                                + plugin.getDescription().version + "§c esta desativado.")
                    }

                }
            }
            p.sendMessage("§aCaso deseje comprar mais plugins entre em contato ou no site §bwww.eduard.com.br")
        }

    }

    @EventHandler
    fun onDeath(e: PlayerDeathEvent) {
        val p = e.entity


        if (Mine.OPT_AUTO_RESPAWN) {
            if (p.hasPermission("eduard.autorespawn")) {
                object : BukkitRunnable() {
                    override fun run() {
                        if (p.isDead) {
                            p.fireTicks = 0
                            try {
                                MineReflect.makeRespawn(p)
                            } catch (ex: Exception) {

                                ex.printStackTrace()
                            }

                        }
                    }
                }.runTask(plugin)


            }

        }
        if (Mine.OPT_NO_DEATH_MESSAGE) {
            e.deathMessage = null
        }
    }

    @EventHandler
    fun onPingServer(e: ServerListPingEvent) {
        val amount = EduardAPI.instance.configs.getInt("custom-motd-amount")
        if (amount > -1) {
            e.maxPlayers = amount!!
        }

        if (EduardAPI.instance.configs.getBoolean("custom-motd")) {
            val builder = StringBuilder()
            for (line in EduardAPI.instance.configs.getMessages("motd")) {
                builder.append(line + "\n")
            }
            e.motd = builder.toString()
        }

    }

    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        val p = e.player
        if (EduardAPI.instance.configs.getBoolean("custom-quit-message"))
            e.quitMessage = Mine.MSG_ON_QUIT.replace("\$player", p.name)
        if (Mine.OPT_NO_QUIT_MESSAGE) {
            e.quitMessage = ""
        }
    }

    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        val p = e.player

        if (EduardAPI.instance.getBoolean("custom-first-join-message")) {
            if (!p.hasPlayedBefore()) {
                e.joinMessage = EduardAPI.instance.message("first-join-message").replace("\$player", p.name)
            }
        } else if (EduardAPI.instance.configs.getBoolean("custom-join-message")) {
            e.joinMessage = Mine.MSG_ON_JOIN.replace("\$player", p.name)
        }


        if (Mine.OPT_NO_JOIN_MESSAGE) {
            e.joinMessage = null
            return
        }

    }


}
