package net.eduard.api.lib.command

import net.md_5.bungee.BungeeCord
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.connection.ProxiedPlayer

class PlayerBungee(proxiedPlayer: ProxiedPlayer) : PlayerOnline<ProxiedPlayer>(proxiedPlayer.name,proxiedPlayer.uniqueId) {

    override var player: ProxiedPlayer = proxiedPlayer
    get(){
        if (!field.isConnected){
            field = BungeeCord.getInstance().getPlayer(name)
        }
        return field
    }


    override fun sendMessage(str: String) {
        player.sendMessage(str)
    }

    override fun sendMessage(str: TextComponent) {
        player.sendMessage(str)
    }

    override fun connect(serverName: String) {
        player.connect(BungeeCord.getInstance().getServerInfo(serverName))
    }

    override var fly = false


    override fun hasPermission(permission: String): Boolean {
        return player.hasPermission(permission)
    }

    override val isOffline: Boolean
        get() = !player.isConnected


}