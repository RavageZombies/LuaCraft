package com.luacraft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.profiler.Profiler;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.luacraft.classes.LuaJavaBlock;

import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.MouseInputEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemPickupEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemSmeltedEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

public class LuaEventManager {
	LuaCraftState l = null;

	public LuaEventManager(LuaCraftState state) {
		l = state;
	}

	// FML Bus

	// Command Events

	/**
	 * @author Jake
	 * @function command.run Calls whenever a command is ran
	 * @arguments [[Player]]:player, [[String]]:command, [[Table]]:arguments
	 * @return nil
	 */

	@SubscribeEvent
	public void onCommand(CommandEvent event) {
		synchronized (l) {
			if (!l.isOpen())
				return;

			try {
				l.pushHookCall();
				l.pushString("command.run");

				if (event.sender instanceof EntityPlayer)
					LuaUserdataManager.PushUserdata(l, (EntityPlayer) event.sender);
				else
					l.pushNil();

				l.pushString(event.command.getName());
				// TODO: add paramaters
				l.call(3, 0);
			} catch (Exception e) {
				l.handleException(e);
			}
		}
	}

	// Input Events

	/**
	 * @author Jake
	 * @function input.keypress Calls whenever a key is pressed
	 * @arguments [[Number]]:key, [[Boolean]]:repeat
	 * @return nil
	 */

	@SubscribeEvent
	public void onKeyInput(KeyInputEvent event) {
		synchronized (l) {
			if (!l.isOpen())
				return;

			try {
				l.pushHookCall();
				l.pushString("input.keypress");
				l.pushNumber(Keyboard.getEventKey());
				l.pushBoolean(Keyboard.isRepeatEvent());
				l.call(3, 0);
			} catch (Exception e) {
				l.handleException(e);
			}
		}
	}

	/**
	 * @author Jake
	 * @function input.mousepress Calls whenever a mouse button is pressed
	 * @arguments [[Number]]:key
	 * @return nil
	 */

	@SubscribeEvent
	public void onMouseInput(MouseInputEvent event) {
		synchronized (l) {
			if (!l.isOpen())
				return;

			try {
				l.pushHookCall();
				l.pushString("input.mousepress");
				l.pushNumber(Mouse.getEventButton());
				l.call(2, 0);
			} catch (Exception e) {
				l.handleException(e);
			}
		}
	}

	// Player Events (ItemCraftedEvent, ItemPickupEvent, ItemSmeltedEvent,
	// PlayerChangedDimensionEvent, PlayerLoggedInEvent, PlayerLoggedOutEvent,
	// PlayerRespawnEvent)

	@SubscribeEvent
	public void onItemCrafted(ItemCraftedEvent event) {
		synchronized (l) {
			if (!l.isOpen())
				return;

			try {
				l.pushHookCall();
				l.pushString("player.craftitem");
				LuaUserdataManager.PushUserdata(l, event.player);
				l.pushUserdataWithMeta(event.crafting, "ItemStack");
				l.call(3, 1);

				if (!l.isNil(-1))
					event.setCanceled(l.toBoolean(-1));

				l.setTop(0);
			} catch (Exception e) {
				l.handleException(e);
			}
		}
	}

	@SubscribeEvent
	public void onItemPickup(ItemPickupEvent event) {
		synchronized (l) {
			if (!l.isOpen())
				return;

			try {
				l.pushHookCall();
				l.pushString("player.pickupitem");
				LuaUserdataManager.PushUserdata(l, event.player);
				LuaUserdataManager.PushUserdata(l, event.pickedUp);
				l.call(3, 0);
			} catch (Exception e) {
				l.handleException(e);
			}
		}
	}

	@SubscribeEvent
	public void onItemSmelted(ItemSmeltedEvent event) {
		synchronized (l) {
			if (!l.isOpen())
				return;

			try {
				l.pushHookCall();
				l.pushString("player.smeltitem");
				LuaUserdataManager.PushUserdata(l, event.player);
				l.pushUserdataWithMeta(event.smelting, "ItemStack");
				l.call(3, 1);

				if (!l.isNil(-1))
					event.setCanceled(l.toBoolean(-1));

				l.setTop(0);
			} catch (Exception e) {
				l.handleException(e);
			}
		}
	}

	@SubscribeEvent
	public void onPlayerChangedDimension(PlayerChangedDimensionEvent event) {
		synchronized (l) {
			if (!l.isOpen())
				return;

			try {
				l.pushHookCall();
				l.pushString("player.changedimension");
				LuaUserdataManager.PushUserdata(l, event.player);
				l.pushNumber(event.fromDim);
				l.pushNumber(event.toDim);
				l.call(4, 1);

				if (!l.isNil(-1))
					event.setCanceled(l.toBoolean(-1));

				l.setTop(0);
			} catch (Exception e) {
				l.handleException(e);
			}
		}
	}

	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerLoggedInEvent event) {
		synchronized (l) {
			if (!l.isOpen())
				return;

			try {
				l.pushHookCall();
				l.pushString("player.connect");
				LuaUserdataManager.PushUserdata(l, event.player);
				l.call(2, 0);
			} catch (Exception e) {
				l.handleException(e);
			}
		}
	}

	@SubscribeEvent
	public void onPlayerLoggedOut(PlayerLoggedOutEvent event) {
		synchronized (l) {
			if (!l.isOpen())
				return;

			try {
				l.pushHookCall();
				l.pushString("player.disconnect");
				LuaUserdataManager.PushUserdata(l, event.player);
				l.call(2, 0);
			} catch (Exception e) {
				l.handleException(e);
			}
		}
	}

	@SubscribeEvent
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		synchronized (l) {
			if (!l.isOpen())
				return;

			try {
				l.pushHookCall();
				l.pushString("player.spawned");
				LuaUserdataManager.PushUserdata(l, event.player);
				l.call(2, 0);
			} catch (Exception e) {
				l.handleException(e);
			}
		}
	}

	// Tick Events (ClientTickEvent, PlayerTickEvent, RenderTickEvent,
	// ServerTickEvent, WorldTickEvent)

	@SubscribeEvent
	public void onServerTick(ServerTickEvent event) {
		if (event.phase == Phase.START)
			return;

		synchronized (l) {
			if (!l.isOpen())
				return;

			try {
				l.pushHookCall();
				l.pushString("game.tick");
				l.call(1, 0);
			} catch (Exception e) {
				l.handleException(e);
			}
		}
	}

	@SubscribeEvent
	public void onClientTick(ClientTickEvent event) {
		if (event.phase == Phase.START)
			return;

		synchronized (l) {
			if (!l.isOpen())
				return;

			try {
				l.pushHookCall();
				l.pushString("game.tick");
				l.call(1, 0);
			} catch (Exception e) {
				l.handleException(e);
			}
		}
	}

	@SubscribeEvent
	public void onRenderTick(RenderTickEvent event) {
		if (event.phase == Phase.START)
			return;

		synchronized (l) {
			if (!l.isOpen())
				return;

			if (l.getMinecraft().thePlayer == null)
				return;

			Profiler luaProfiler = l.getMinecraft().mcProfiler;

			// root.render
			if (event.phase == Phase.START)
				luaProfiler.endSection();

			luaProfiler.startSection("luacraft");
			luaProfiler.startSection("tick");

			try {
				l.pushHookCall();
				l.pushString("render.tick");
				l.pushNumber(event.renderTickTime);
				l.pushNumber(event.phase.ordinal());
				l.call(3, 0);

			} catch (Exception e) {
				l.handleException(e);
			}

			luaProfiler.endSection();
			luaProfiler.endSection();

			// root.render
			if (event.phase == Phase.START)
				luaProfiler.startSection("render");
		}
	}

	// Minecraft Bus

	@SubscribeEvent
	public void onServerChat(ServerChatEvent event) {
		synchronized (l) {
			if (!l.isOpen())
				return;

			try {
				l.pushHookCall();
				l.pushString("player.say");
				LuaUserdataManager.PushUserdata(l, event.player);
				l.pushString(event.message);
				l.call(3, 1);

				if (!l.isNil(-1))
					event.setCanceled(l.toBoolean(-1));

				l.setTop(0);
			} catch (Exception e) {
				l.handleException(e);
			}
		}
	}

	// Brewing Events

	// TODO: PotionBrewedEvent

	// Item Events

	@SubscribeEvent
	public void onItemExpire(ItemExpireEvent event) {
		synchronized (l) {
			if (!l.isOpen())
				return;

			try {
				l.pushHookCall();
				l.pushString("item.expired");
				LuaUserdataManager.PushUserdata(l, event.entityItem);
				l.call(2, 1);

				if (!l.isNil(-1))
					event.setCanceled(l.toBoolean(-1));

				l.setTop(0);
			} catch (Exception e) {
				l.handleException(e);
			}
		}
	}

	@SubscribeEvent
	public void onItemToss(ItemTossEvent event) {
		synchronized (l) {
			if (!l.isOpen())
				return;

			try {
				l.pushHookCall();
				l.pushString("player.dropitem");
				LuaUserdataManager.PushUserdata(l, event.player);
				LuaUserdataManager.PushUserdata(l, event.entityItem);
				l.call(3, 1);

				if (!l.isNil(-1))
					event.setCanceled(l.toBoolean(-1));

				l.setTop(0);
			} catch (Exception e) {
				l.handleException(e);
			}
		}
	}

	// Entity Events

	@SubscribeEvent
	public void onEntityStruckByLightning(EntityStruckByLightningEvent event) {
		synchronized (l) {
			if (!l.isOpen())
				return;

			try {
				l.pushHookCall();
				l.pushString("entity.lightning");
				LuaUserdataManager.PushUserdata(l, event.entity);
				LuaUserdataManager.PushUserdata(l, event.lightning);
				l.call(3, 1);

				if (!l.isNil(-1))
					event.setCanceled(l.toBoolean(-1));

				l.setTop(0);
			} catch (Exception e) {
				l.handleException(e);
			}
		}
	}

	@SubscribeEvent
	public void onEntityJoinedWorld(EntityJoinWorldEvent event) {
		synchronized (l) {
			if (!l.isOpen())
				return;

			try {
				l.pushHookCall();
				l.pushString("entity.joinworld");
				LuaUserdataManager.PushUserdata(l, event.entity);
				LuaUserdataManager.PushUserdata(l, event.world);
				l.call(3, 1);

				if (!l.isNil(-1))
					event.setCanceled(l.toBoolean(-1));

				l.setTop(0);
			} catch (Exception e) {
				l.handleException(e);
			}
		}
	}

	// Living Events

	@SubscribeEvent
	public void onLivingSpawned(LivingSpawnEvent.CheckSpawn event) {
		synchronized (l) {
			if (!l.isOpen())
				return;

			try {
				l.pushHookCall();
				l.pushString("entity.spawned");
				LuaUserdataManager.PushUserdata(l, event.entity);
				l.call(2, 1);

				event.setResult(Result.values()[l.checkInteger(-1, Result.DEFAULT.ordinal())]);
				l.setTop(0);
			} catch (Exception e) {
				l.handleException(e);
			}
		}
	}

	// @SubscribeEvent
	public void onLivingRemoved(LivingSpawnEvent.AllowDespawn event) {
		synchronized (l) {
			if (!l.isOpen())
				return;

			try {
				l.pushHookCall();
				l.pushString("entity.removed");
				LuaUserdataManager.PushUserdata(l, event.entity);
				l.call(2, 1);

				event.setResult(Result.values()[l.checkInteger(-1, Result.DEFAULT.ordinal())]);
				l.setTop(0);
			} catch (Exception e) {
				l.handleException(e);
			}
		}
	}

	@SubscribeEvent
	public void onLivingAttack(LivingAttackEvent event) {
		synchronized (l) {
			if (!l.isOpen())
				return;

			try {
				l.pushHookCall();
				l.pushString("entity.attacked");
				LuaUserdataManager.PushUserdata(l, event.entity);
				l.pushNumber(event.ammount);
				l.pushUserdataWithMeta(event.source, "DamageSource");
				l.call(4, 1);

				if (!l.isNil(-1))
					event.setCanceled(l.toBoolean(-1));

				l.setTop(0);
			} catch (Exception e) {
				l.handleException(e);
			}
		}
	}

	@SubscribeEvent
	public void onLivingDeath(LivingDeathEvent event) {
		synchronized (l) {
			if (!l.isOpen())
				return;

			try {
				l.pushHookCall();
				l.pushString("entity.death");
				LuaUserdataManager.PushUserdata(l, event.entity);
				l.pushUserdataWithMeta(event.source, "DamageSource");
				l.call(3, 1);

				if (!l.isNil(-1))
					event.setCanceled(l.toBoolean(-1));

				l.setTop(0);
			} catch (Exception e) {
				l.handleException(e);
			}
		}
	}

	// TODO: LivingDropsEvent

	@SubscribeEvent
	public void onLivingFall(LivingFallEvent event) {
		synchronized (l) {
			if (!l.isOpen())
				return;

			try {
				l.pushHookCall();
				l.pushString("entity.fall");
				LuaUserdataManager.PushUserdata(l, event.entity);
				l.pushNumber(event.distance);
				l.call(3, 1);

				if (!l.isNil(-1))
					event.setCanceled(l.toBoolean(-1));

				l.setTop(0);
			} catch (Exception e) {
				l.handleException(e);
			}
		}
	}

	// TODO: LivingHurtEvent (same as LivingAttackEvent)

	@SubscribeEvent
	public void onLivingJump(LivingJumpEvent event) {
		synchronized (l) {
			if (!l.isOpen())
				return;

			try {
				l.pushHookCall();
				l.pushString("entity.jump");
				LuaUserdataManager.PushUserdata(l, event.entity);
				l.call(2, 0);
			} catch (Exception e) {
				l.handleException(e);
			}
		}
	}

	// @SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent event) {
		synchronized (l) {
			if (!l.isOpen())
				return;

			// Profiler luaProfiler = l.getMinecraft().mcProfiler;

			// if (luaProfiler.profilingEnabled)
			// System.out.println(luaProfiler.getNameOfLastSection());

			// l.GetMinecraft().mcProfiler.startSection("luacraft_update");

			try {
				l.pushHookCall();
				l.pushString("entity.update");
				LuaUserdataManager.PushUserdata(l, event.entity);
				l.call(2, 0);
			} catch (Exception e) {
				l.handleException(e);
			}

			// l.GetMinecraft().mcProfiler.endSection();
		}
	}

	// Player Events

	// TODO: ArrowLooseEvent

	// TODO: ArrowNockEvent

	// TODO: AttackEntityEvent (covered by living?)

	// TODO: BonemealEvent

	// TODO: BreakSpeed

	@SubscribeEvent
	public void onEntityInteract(EntityInteractEvent event) {
		synchronized (l) {
			if (!l.isOpen())
				return;

			try {
				l.pushHookCall();
				l.pushString("player.interact");
				LuaUserdataManager.PushUserdata(l, event.entityPlayer);
				LuaUserdataManager.PushUserdata(l, event.target);
				l.call(3, 1);

				if (!l.isNil(-1))
					event.setCanceled(l.toBoolean(-1));

				l.setTop(0);
			} catch (Exception e) {
				l.handleException(e);
			}
		}
	}

	// TODO: EntityItemPickupEvent

	// TODO: FillBucketEvent

	// TODO: HarvestCheck

	// TODO: ItemTooltipEvent

	// TODO: PlayerDestroyItemEvent

	@SubscribeEvent
	public void onPlayerDrops(PlayerDropsEvent event) {
		// Child class of LivingDropsEvent that is fired specifically when a
		// player dies. Canceling the event will prevent ALL drops from entering
		// the world.
		synchronized (l) {
			if (!l.isOpen())
				return;

			try {
				l.pushHookCall();
				l.pushString("player.dropall");
				LuaUserdataManager.PushUserdata(l, event.entityPlayer);
				l.call(2, 1);

				if (!l.isNil(-1))
					event.setCanceled(l.toBoolean(-1));

				l.setTop(0);
			} catch (Exception e) {
				l.handleException(e);
			}
		}
	}

	// TODO: PlayerFlyableFallEvent

	@SubscribeEvent
	public void onPlayerInteract(PlayerInteractEvent event) {
		synchronized (l) {
			if (!l.isOpen())
				return;

			if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR)
				return;

			try {
				l.pushHookCall();
				l.pushString("player.mineblock");
				LuaUserdataManager.PushUserdata(l, event.entityPlayer);

				LuaJavaBlock thisBlock = new LuaJavaBlock(event.entityPlayer.worldObj, event.pos);
				LuaUserdataManager.PushUserdata(l, thisBlock);

				l.pushNumber(event.action.ordinal());
				l.pushFace(event.face);
				l.call(5, 1);

				if (!l.isNil(-1))
					event.setCanceled(l.toBoolean(-1));

				l.setTop(0);
			} catch (Exception e) {
				l.handleException(e);
			}
		}
	}

	// TODO: PlayerOpenContainerEvent
	// This event is fired when a player attempts to view a container during
	// player tick.
	// setResult ALLOW to allow the container to stay open setResult DENY to
	// force close the container (denying access)

	// TODO: PlayerPickupXpEvent
	// This event is called when a player collides with a EntityXPOrb on the
	// ground. The event can be canceled, and no further processing will be
	// done.

	// TODO: PlayerSleepInBedEvent

	// TODO: PlayerUseItemEvent.Start / Stop / Finish / Tick

	// TODO: UseHoeEvent
}