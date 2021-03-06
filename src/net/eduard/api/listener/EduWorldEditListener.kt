package net.eduard.api.listener

import net.eduard.api.EduardAPI
import net.eduard.api.lib.manager.EventsManager
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

/**
 * Listener para meu Sistema de WorldEdit customizado
 *
 * @author Eduard
 */
class EduWorldEditListener : EventsManager() {


    @EventHandler
    fun onSelectPositions(e: PlayerInteractEvent) {
        val p = e.player
        if (p.gameMode == GameMode.CREATIVE) {
            if (e.item == null)
                return
            if (e.item.type == Material.WOOD_AXE) {
                val mapa = EduardAPI.getSchematic(p)
                if (e.action == Action.LEFT_CLICK_BLOCK) {
                    mapa.high = e.clickedBlock.location.toVector()
                    p.sendMessage("§aPosição 1 setada!")
                } else if (e.action == Action.RIGHT_CLICK_BLOCK) {
                    mapa.low = e.clickedBlock.location.toVector()
                    p.sendMessage("§aPosição 2 setada!")
                }

            }
        }
    }

}
