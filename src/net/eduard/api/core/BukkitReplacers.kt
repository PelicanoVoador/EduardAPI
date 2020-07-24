package net.eduard.api.core

import net.eduard.api.lib.kotlin.format
import net.eduard.api.lib.modules.Extra
import net.eduard.api.lib.modules.Mine
import net.eduard.api.lib.modules.VaultAPI
import org.bukkit.Statistic
import org.bukkit.entity.Player

/**
 * Registrando os replacers mais usados na Displayboard
 *
 * @author Eduard
 */
class BukkitReplacers {
    init {
        if (Mine.hasPlugin("Vault")) {
            Mine.addReplacer("\$player_group") { VaultAPI.getPermission().getPrimaryGroup(it) }
            Mine.addReplacer("\$player_prefix") { Mine.toChatMessage(VaultAPI.getChat().getPlayerPrefix(it)) }
            Mine.addReplacer("\$player_suffix") { Mine.toChatMessage(Mine.toChatMessage(VaultAPI.getChat().getPlayerPrefix(it))) }
            Mine.addReplacer("\$group_prefix") {
                Mine.toChatMessage(
                        VaultAPI.getChat().getGroupPrefix("null", VaultAPI.getPermission().getPrimaryGroup(it)))
            }
            Mine.addReplacer("\$group_suffix") {
                Mine.toChatMessage(
                        VaultAPI.getChat().getGroupSuffix("null", VaultAPI.getPermission().getPrimaryGroup(it)))
            }
            Mine.addReplacer("\$player_money") {
                if (VaultAPI.hasVault() && VaultAPI.hasEconomy()) {
                    return@addReplacer Extra.MONEY.format(VaultAPI.getEconomy().getBalance(it))
                }
                "0.00"
            }
            Mine.addReplacer("\$player_balance") {
                if (VaultAPI.hasVault() && VaultAPI.hasEconomy()) {
                    return@addReplacer Extra.formatMoney(VaultAPI.getEconomy().getBalance(it))
                }
                "0.00"
            }
        }
        Mine.addReplacer("\$players_online") { Mine.getPlayers().size }
        Mine.addReplacer("\$player_world") { it.world.name }
        Mine.addReplacer("\$player_display_name") { it.displayName }
        Mine.addReplacer("\$player_name") { it.name }
        Mine.addReplacer("\$player_health") { it.health.format() }
        Mine.addReplacer("\$player_max_health") { it.maxHealth.format() }
        Mine.addReplacer("\$player_level") { it.level }
        Mine.addReplacer("\$player_xp") { Extra.MONEY.format(it.totalExperience.toLong()) }
        Mine.addReplacer("\$player_kills") { it.getStatistic(Statistic.PLAYER_KILLS) }
        Mine.addReplacer("\$player_deaths") { it.getStatistic(Statistic.DEATHS) }
        Mine.addReplacer("\$player_kdr") {
            val kill = it.getStatistic(Statistic.PLAYER_KILLS)
            val death = it.getStatistic(Statistic.DEATHS)
            if (kill == 0) return@addReplacer 0
            if (death == 0) return@addReplacer 0
            kill / death
        }
        Mine.addReplacer("\$player_kill/death") {
            val kill = it.getStatistic(Statistic.PLAYER_KILLS)
            val death = it.getStatistic(Statistic.DEATHS)
            if (kill == 0) return@addReplacer 0
            if (death == 0) return@addReplacer 0
            kill / death
        }
        Mine.addReplacer("\$player_x") { it.location.x }
        Mine.addReplacer("\$player_y") { it.location.y }
        Mine.addReplacer("\$player_z") { it.location.z }
    }
}