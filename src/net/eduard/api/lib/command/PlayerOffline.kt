package net.eduard.api.lib.command

import net.eduard.api.lib.storage.Storable
import net.md_5.bungee.BungeeCord
import org.bukkit.Bukkit
import java.io.Serializable
import java.util.*

@Storable.StorageAttributes(inline = true)
class PlayerOffline(var name: String = "Eduard",
                    uuid: UUID? = null): Serializable {

    lateinit var uniqueId: UUID

    init {
        if (uuid == null) {
            setUUIDByName()
        } else {
            uniqueId = uuid
        }
    }

    @Transient
    private var onlinePlayer: PlayerOnline<*>? = null

    fun setUUIDByName(): UUID {
        uniqueId = UUID.nameUUIDFromBytes(("OfflinePlayer:$name").toByteArray())
        return uniqueId
    }

    fun sendMessage(message: String) {
        onlinePlayer?.sendMessage(message)
    }

    constructor(name : String): this(name,null)


    constructor(player: PlayerOnline<*>) : this(player.name, player.uniqueId) {
        this.onlinePlayer = player
    }

    val isOnline: Boolean
        get() {
            return try {
                Bukkit.getPlayer(name) != null
            } catch (er: Error) {
                BungeeCord.getInstance().getPlayer(name) != null
            }
        }


    val player: PlayerOnline<*>
        get() {
            if (onlinePlayer == null||onlinePlayer!!.isOffline) {
                onlinePlayer = try {
                    PlayerBukkit(Bukkit.getPlayer(name))
                } catch (er: Error) {
                    PlayerBungee(BungeeCord.getInstance().getPlayer(name))
                }
            }
            return onlinePlayer!!
        }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (this === other) return true
        if (other is PlayerOffline) {
            return (name.equals(other.name, true))
        }
        return true
    }

    override fun hashCode(): Int {
        return name.toLowerCase().hashCode()
    }


}