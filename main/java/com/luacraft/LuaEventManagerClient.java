package com.luacraft;

import com.luacraft.classes.Vector;
import com.naef.jnlua.LuaRuntimeException;

import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LuaEventManagerClient {
	private final LuaCraftState l;

	public LuaEventManagerClient(LuaCraftState state) {
		l = state;
	}

	/**
	 * @author Jake
	 * @function input.mousemove
	 * @info Calls whenever the mouse is moved
	 * @arguments [[Number]]:button, [[Number]]:x, [[Number]]:y
	 * @return nil
	 */

	@SubscribeEvent
	public void onMouse(MouseEvent event) {
		synchronized (l) {
			if (!l.isOpen())
				return;

			if (l.getMinecraft().player == null)
				return;

			try {
				l.pushHookCall();
				l.pushString("input.mousemove");
				l.pushNumber(event.getButton());
				l.pushNumber(event.getX());
				l.pushNumber(event.getY());
				l.call(4, 0);
			} catch (LuaRuntimeException e) {
				l.handleLuaRuntimeError(e);
			} finally {
				l.setTop(0);
			}
		}
	}

	/**
	 * @author Jake
	 * @function render.gameoverlay.*
	 * @info Calls whenever a frame is drawn Used for rendering 2D text, textures, etc.
	 * @arguments [[Number]]:ticks, [[Number]]:type
	 * @return nil
	 */

	@SubscribeEvent
	public void onRenderGameOverlay(RenderGameOverlayEvent event) {
		synchronized (l) {
			if (l.getMinecraft().player == null)
				return;

			if (!l.isOpen())
				return;

			try {
				l.pushHookCall();
				switch(event.getType()) {
					case ALL:
						l.pushString("render.gameoverlay.all");
						break;
					case HELMET:
						l.pushString("render.gameoverlay.helmet");
						break;
					case PORTAL:
						l.pushString("render.gameoverlay.portal");
						break;
					case CROSSHAIRS:
						l.pushString("render.gameoverlay.crosshairs");
						break;
					case BOSSHEALTH:
						l.pushString("render.gameoverlay.bosshealth");
						break;
					case BOSSINFO:
						l.pushString("render.gameoverlay.bossinfo");
						break;
					case ARMOR:
						l.pushString("render.gameoverlay.armor");
						break;
					case HEALTH:
						l.pushString("render.gameoverlay.health");
						break;
					case FOOD:
						l.pushString("render.gameoverlay.food");
						break;
					case AIR:
						l.pushString("render.gameoverlay.air");
						break;
					case HOTBAR:
						l.pushString("render.gameoverlay.hotbar");
						break;
					case EXPERIENCE:
						l.pushString("render.gameoverlay.experience");
						break;
					case TEXT:
						l.pushString("render.gameoverlay.text");
						break;
					case HEALTHMOUNT:
						l.pushString("render.gameoverlay.healthmount");
						break;
					case JUMPBAR:
						l.pushString("render.gameoverlay.jumpbar");
						break;
					case CHAT:
						l.pushString("render.gameoverlay.chat");
						break;
					case PLAYER_LIST:
						l.pushString("render.gameoverlay.player_list");
						break;
					case DEBUG:
						l.pushString("render.gameoverlay.debug");
						break;
					case POTION_ICONS:
						l.pushString("render.gameoverlay.potion_icons");
						break;
					case SUBTITLES:
						l.pushString("render.gameoverlay.subtitles");
						break;
					case FPS_GRAPH:
						l.pushString("render.gameoverlay.fpsgraph");
						break;
					case VIGNETTE:
						l.pushString("render.gameoverlay.vignette");
						break;
					default:
						l.pushString("render.gameoverlay.unimplemented");
						break;
				}
				l.pushNumber(event.getPartialTicks());
				l.pushNumber(event.getType().ordinal());
				l.call(3, event.isCancelable() ? 1 : 0);

				if(event.isCancelable() && l.isBoolean(-1))
					event.setCanceled(l.toBoolean(-1));

			} catch (LuaRuntimeException e) {
				l.handleLuaRuntimeError(e);
			} finally {
				l.setTop(0);
			}
		}
	}

	/**
	 * @author Jake
	 * @function render.world
	 * @info Calls whenever a frame within the world is drawn Used for rendering 3D objects
	 * @arguments [[Number]]:ticks
	 * @return nil
	 */

	@SubscribeEvent
	public void onRenderWorld(RenderWorldLastEvent event) {
		synchronized (l) {
			if (!l.isOpen())
				return;

			if (l.getMinecraft().player == null)
				return;

			try {
				l.pushHookCall();
				l.pushString("render.world");
				l.pushNumber(event.getPartialTicks());
				l.call(2, 0);
			} catch (LuaRuntimeException e) {
				l.handleLuaRuntimeError(e);
			} finally {
				l.setTop(0);
			}
		}
	}

	/**
	 * @author Jake
	 * @function player.prerender
	 * @info Called before the player is drawn
	 * @arguments [[Player]]:player
	 * @return nil
	 */

	@SubscribeEvent
	public void onPreRenderPlayer(RenderPlayerEvent.Pre event) {
		synchronized (l) {
			if (!l.isOpen())
				return;

			try {
				l.pushHookCall();
				l.pushString("player.prerender");
				LuaUserdata.PushUserdata(l, event.getEntityPlayer());
				l.pushNumber(event.getPartialRenderTick());
				l.call(3, 0);
			} catch (LuaRuntimeException e) {
				l.handleLuaRuntimeError(e);
			} finally {
				l.setTop(0);
			}
		}
	}

	/**
	 * @author Jake
	 * @function player.postrender
	 * @info Called after the player is drawn
	 * @arguments [[Player]]:player
	 * @return nil
	 */

	@SubscribeEvent
	public void onPostRenderPlayer(RenderPlayerEvent.Post event) {
		synchronized (l) {
			if (!l.isOpen())
				return;

			try {
				l.pushHookCall();
				l.pushString("player.postrender");
				LuaUserdata.PushUserdata(l, event.getEntityPlayer());
				l.pushNumber(event.getPartialRenderTick());
				l.call(3, 0);
			} catch (LuaRuntimeException e) {
				l.handleLuaRuntimeError(e);
			} finally {
				l.setTop(0);
			}
		}
	}

	/**
	 * @author fr1kin
	 * @function entity.prerender
	 * @info Called before a entity is drawn
	 * @arguments [[Entity]]:entity, [[RenderLivingBase]]:renderer, [[Vector]]:pos
	 * @return [[bool]]:cancel
	 */
	@SubscribeEvent
	public void onPreRenderEntity(RenderLivingEvent.Pre event) {
		synchronized (l) {
			if (!l.isOpen())
				return;

			try {
				l.pushHookCall();
				l.pushString("entity.prerender");
				LuaUserdata.PushUserdata(l, event.getEntity());
				l.pushUserdataWithMeta(event.getRenderer(), "RenderLivingBase");
				Vector vec = new Vector(event.getX(), event.getZ(), event.getY());
				vec.push(l);
				l.call(4, 1);

				if(l.isBoolean(-1))
					event.setCanceled(l.toBoolean(-1));

			} catch (LuaRuntimeException e) {
				l.handleLuaRuntimeError(e);
			} finally {
				l.setTop(0);
			}
		}
	}

	/**
	 * @author fr1kin
	 * @function entity.postrender
	 * @info Called after a entity is drawn
	 * @arguments [[Entity]]:entity, [[RenderLivingBase]]:renderer, [[Vector]]:pos
	 * @return
	 */
	@SubscribeEvent
	public void onPostRenderEntity(RenderLivingEvent.Post event) {
		synchronized (l) {
			if (!l.isOpen())
				return;

			try {
				l.pushHookCall();
				l.pushString("entity.postrender");
				LuaUserdata.PushUserdata(l, event.getEntity());
				l.pushUserdataWithMeta(event.getRenderer(), "RenderLivingBase");
				Vector vec = new Vector(event.getX(), event.getZ(), event.getY());
				vec.push(l);
				l.call(4, 0);
			} catch (LuaRuntimeException e) {
				l.handleLuaRuntimeError(e);
			} finally {
				l.setTop(0);
			}
		}
	}

	/**
	 * @author CapsAdmin
	 * @function mainmenu.init
	 * @info Called when the main menu is initialized
	 * @arguments nil
	 * @return nil
	 */

	@SubscribeEvent
	public void onGuiOpenEvent(GuiOpenEvent event) {
		synchronized (l) {
			if (!l.isOpen())
				return;

			if (event.getGui() instanceof GuiMainMenu) {
				try {
					l.pushHookCall();
					l.pushString("mainmenu.init");
					l.call(1, 0);
				} catch (LuaRuntimeException e) {
					l.handleLuaRuntimeError(e);
				}
			}
		}
	}
}
