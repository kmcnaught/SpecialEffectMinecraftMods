/**
 * Copyright (C) 2016 Kirsty McNaught, SpecialEffect
 * www.specialeffect.org.uk
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.specialeffect.mods.misc;

import org.lwjgl.input.Keyboard;

import com.specialeffect.callbacks.BaseClassWithCallbacks;
import com.specialeffect.messages.SendCommandMessage;
import com.specialeffect.mods.EyeGaze;
import com.specialeffect.utils.CommonStrings;
import com.specialeffect.utils.ModUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = SwapMinePlace.MODID, version = ModUtils.VERSION, name = SwapMinePlace.NAME)
public class SwapMinePlace extends BaseClassWithCallbacks {
	public static final String MODID = "specialeffect.swapmineplace";
	public static final String NAME = "SwapMinePlace";
	public static SimpleNetworkWrapper network;

	@EventHandler
	@SuppressWarnings("static-access")
	public void preInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);

		ModUtils.setupModInfo(event, this.MODID, this.NAME, "Add key binding to swap mine/place key bindings.");
		ModUtils.setAsParent(event, EyeGaze.MODID);

		// Register for server messages
		network = NetworkRegistry.INSTANCE.newSimpleChannel(this.NAME);
		network.registerMessage(SendCommandMessage.Handler.class, SendCommandMessage.class, 0, Side.SERVER);

	}

	@EventHandler
	public void init(FMLInitializationEvent event) {

		// Register key bindings
		mSwapKB = new KeyBinding("Swap mine/place keys", Keyboard.KEY_F10, CommonStrings.EYEGAZE_EXTRA);
		ClientRegistry.registerKeyBinding(mSwapKB);		
	}
	
	@SubscribeEvent
	public void onLiving(LivingUpdateEvent event) {
		if (ModUtils.entityIsMe(event.getEntityLiving())) {
			this.processQueuedCallbacks(event);
		}
	}

	private static KeyBinding mSwapKB;

	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent event) {
		if (mSwapKB.isPressed()) {
			KeyBinding kbAttack = Minecraft.getMinecraft().gameSettings.keyBindAttack;
			KeyBinding kbPlace = Minecraft.getMinecraft().gameSettings.keyBindUseItem;
			Minecraft.getMinecraft().gameSettings.keyBindAttack = kbPlace;
			Minecraft.getMinecraft().gameSettings.keyBindUseItem = kbAttack;			
			this.queueChatMessage("Swapping mine and place keys");			
		}
	}
}
