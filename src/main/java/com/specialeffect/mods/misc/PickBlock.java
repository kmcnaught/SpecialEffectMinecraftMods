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


import com.specialeffect.callbacks.BaseClassWithCallbacks;
import com.specialeffect.utils.CommonStrings;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings.Input;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;

import org.lwjgl.glfw.GLFW;



@Mod(PickBlock.MODID)
public class PickBlock  {
	public static final String MODID = "pickblock";
	public static final String NAME = "PickBlock";

	public static KeyBinding mPickBlockKB;

	public PickBlock() {
	    
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);      
        
        // Register key bindings
		mPickBlockKB = new KeyBinding("Pick block", GLFW.GLFW_KEY_KP_2, CommonStrings.EYEGAZE_COMMON);
		ClientRegistry.registerKeyBinding(mPickBlockKB);
    }
	
	@SubscribeEvent
    public void onKeyInput(KeyInputEvent event) {   
		if (mPickBlockKB.isPressed()) {
	        final Input pickBlockKey = Minecraft.getInstance().gameSettings.keyBindPickBlock.getKey();
			KeyBinding.onTick(pickBlockKey);
		}
    }
	
}
