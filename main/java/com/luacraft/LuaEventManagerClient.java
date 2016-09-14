package com.luacraft;

import com.naef.jnlua.LuaRuntimeException;

import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.gui.GuiMainMenu;

public class LuaEventManagerClient {
	LuaCraftState l = null;

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

			if (l.getMinecraft().thePlayer == null)
				return;

			try {
				l.pushHookCall();
				l.pushString("input.mousemove");
				l.pushNumber(event.button);
				l.pushNumber(event.x);
				l.pushNumber(event.y);
				l.call(4, 0);
			} catch (LuaRuntimeException e) {
				l.handleLuaError(e);
			}
		}
	}

	/**
	 * @author Jake
	 * @function render.gameoverlay
	 * @info Calls whenever a frame is drawn Used for rendering 2D text, textures, etc.
	 * @arguments [[Number]]:ticks, [[Number]]:x, [[Number]]:y
	 * @return nil
	 */

	@SubscribeEvent
	public void onRenderGameOverlay(RenderGameOverlayEvent event) {
		synchronized (l) {
			if (event.type != RenderGameOverlayEvent.ElementType.ALL)
				return;

			if (l.getMinecraft().thePlayer == null)
				return;

			if (!l.isOpen())
				return;

			try {
				l.pushHookCall();
				l.pushString("render.gameoverlay");
				l.pushNumber(event.partialTicks);
				l.call(2, 0);
			} catch (LuaRuntimeException e) {
				l.handleLuaError(e);
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

			if (l.getMinecraft().thePlayer == null)
				return;

			try {
				l.pushHookCall();
				l.pushString("render.world");
				l.pushNumber(event.partialTicks);
				l.call(2, 0);
			} catch (LuaRuntimeException e) {
				l.handleLuaError(e);
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
				LuaUserdata.PushUserdata(l, event.entityPlayer);
				l.pushNumber(event.partialRenderTick);
				l.call(3, 0);
			} catch (LuaRuntimeException e) {
				l.handleLuaError(e);
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
				LuaUserdata.PushUserdata(l, event.entityPlayer);
				l.pushNumber(event.partialRenderTick);
				l.call(3, 0);
			} catch (LuaRuntimeException e) {
				l.handleLuaError(e);
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

			if (event.gui instanceof GuiMainMenu) {
				try {
					l.pushHookCall();
					l.pushString("mainmenu.init");
					l.call(1, 0);
				} catch (LuaRuntimeException e) {
					l.handleLuaError(e);
				}
			}
		}
	}
}
