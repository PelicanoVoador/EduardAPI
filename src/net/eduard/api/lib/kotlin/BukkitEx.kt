package net.eduard.api.lib.kotlin

import net.eduard.api.lib.modules.Extra
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.CropState
import org.bukkit.Material
import org.bukkit.block.BlockState
import org.bukkit.command.CommandExecutor
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.material.Crops
import org.bukkit.plugin.java.JavaPlugin
import kotlin.reflect.KClass

fun Listener.register(plugin: JavaPlugin) = Bukkit.getPluginManager().registerEvents(this, plugin)

fun CommandExecutor.register(cmd: String, plugin: JavaPlugin) {
    plugin.getCommand(cmd).executor = this
}


val Class<*>.plugin: JavaPlugin
    get() {
        if (!JavaPlugin::class.java.isAssignableFrom(this)) {
            return JavaPlugin.getProvidingPlugin(this)
        }
        return JavaPlugin.getPlugin(this as Class<out JavaPlugin>) as JavaPlugin
    }

inline fun Inventory.setItem(line: Int, column: Int, item: ItemStack?) = this.setItem(Extra.getIndex(column, line), item)

val BlockState.isCrop get() = type == Material.CROPS


val BlockState.plantState: CropState?
    get() = if (type == Material.CROPS) (this as Crops).state
    else null


inline val InventoryClickEvent.player get() = this.whoClicked as Player

inline val InventoryOpenEvent.player get() = this.player as Player


inline fun Player.inventory(name: String, lineAmount: Int, block: Inventory.() -> Unit): Inventory {

    val inventory = Bukkit.createInventory(this, 9 * lineAmount, name.cut(32))

    block(inventory)
    player.openInventory(inventory)

    return inventory

}

inline fun Inventory.item(position: Int, block: ItemStack.() -> Unit): ItemStack {

    val item = ItemStack(Material.STONE)

    block(item)

    setItem(position, item)

    return item

}

var ItemStack.name: String
    get() {
        return itemMeta.displayName ?: ""
    }
    set(value) {
        val meta = itemMeta
        meta.displayName = value
        this.itemMeta = meta

    }

var ItemStack.lore: List<String>
    get() {
        return itemMeta.lore ?: listOf()
    }
    set(value) {
        val meta = itemMeta
        meta.lore = value
        this.itemMeta = meta

    }


inline operator fun ItemStack.invoke(name: String): ItemStack {
    this.name = name
    return this
}

inline operator fun ItemStack.invoke(enchament: Enchantment, level: Int): ItemStack {
    addUnsafeEnchantment(enchament, level)
    return this
}

inline operator fun ItemStack.minus(enchament: Enchantment): ItemStack {
    removeEnchantment(enchament)
    return this
}

inline operator fun ItemStack.plus(amount: Int): ItemStack {
    this.amount += amount
    return this
}

inline operator fun ItemStack.plus(map: Map<Enchantment, Int>): ItemStack {
    addUnsafeEnchantments(map)
    return this
}

inline infix fun Enchantment.level(level: Int): Map<Enchantment, Int> {
    return mapOf(this to level)
}

inline fun ItemStack.id(id: Int): ItemStack {
    typeId = id
    return this
}

inline fun ItemStack.data(data: Int): ItemStack {
    durability = data.toShort()
    return this
}

fun ItemStack.addLore(vararg lore: String): ItemStack {
    this.lore = lore.toList()
    return this
}

inline fun ItemStack.lore(vararg lore: String): ItemStack {
    this.lore = lore.toList()
    return this
}

inline fun ItemStack.addEnchant(ench: Enchantment, level: Int): ItemStack {
    addUnsafeEnchantment(ench, level)
    return this
}

fun ItemStack.color(color: Color): ItemStack {
    if (!type.name.contains("LEATHER"))
        type = Material.LEATHER_CHESTPLATE
    val meta = itemMeta as LeatherArmorMeta
    meta.color = color
    itemMeta = meta
    return this
}


fun <T : Event> KClass<T>.event(actionToDo: T.() -> Unit) {
    Bukkit.getPluginManager()
            .registerEvent(this.java,
                    BukkitEventListener, EventPriority.NORMAL, { _, event ->
                actionToDo(event as T)
            }, BukkitEventListener.javaClass.plugin)
}


inline fun <reified T : Event> event(noinline actionToDo: T.() -> Unit) {
    T::class.event(actionToDo)
}




