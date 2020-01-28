package com.specialeffect.inventory;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.KeyEvent;

import org.lwjgl.glfw.GLFW;

import com.specialeffect.utils.ModUtils;

import net.java.games.input.Mouse;
import net.minecraft.client.Minecraft;

/**
 * Manages a Inventory GUI Inventory.
 */
public class CreativeInventoryManager {

	private static CreativeInventoryManager instance = null;

    private Robot robot;
    
    private final int NUM_TABS = 12;
	private final int NUM_COLS = 9;
	private final int NUM_ROWS = 5;

	/**
	 * Creates a new Inventory Manager with the given container.
	 *
	 * @param container The container from a crafting GUI
	 */
	private CreativeInventoryManager() {
		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns a Inventory Manager Instance operating on the given container
	 *
	 * @param container A container from a GUI
	 * @return manager-singleton
	 */
	public static CreativeInventoryManager getInstance(int left, int top, 
													   int xSize, int ySize,
													   int currTab) {
		if (instance == null) {
			instance = new CreativeInventoryManager();
		} 
		instance.updateCoordinates(left, top, xSize, ySize);
		if (instance.currTab != currTab) {
			instance.onTabChanged();
			instance.currTab = currTab;
		}
		return instance;
	}
		
	
	private int tabWidth = 0; // width between centres of consecutive tabs
	private int itemWidth = 0; // width between centres of consecutive items
	
	private int topRowYPos = 0;
	private int bottomRowYPos = 0;
	private int topItemYPos = 0;
	
	private int leftColXPos = 0;
	private int leftItemXPos = 0;
	
	private float xScale = 1.0f;
	private float yScale = 1.0f;
	
	private int currTab;
	
	private void onTabChanged() {
		// reset to hovering over first item when changing tabs
		itemRow = -1;
		itemCol = -1;
	}
	
	private void updateCoordinates(int left, int top, int width, int height) {
		int inventoryWidth = width;
		this.tabWidth = (int) (inventoryWidth/6.9);
		this.itemWidth = (int) (inventoryWidth/10.8);
		this.bottomRowYPos = top - tabWidth/2;
		this.topRowYPos = top + height + tabWidth/2;
		this.topItemYPos = (int) (top + height - itemWidth*1.5);

		this.leftColXPos = left + tabWidth/2;
		this.leftItemXPos = (int) (left + itemWidth*0.9);
		
		// Sizes need scaling before turning into click locations
		Minecraft mc = Minecraft.getInstance();
//		Point size = ModUtils.getScaledDisplaySize(mc);		
//		this.xScale = (float) (mc.currentScreen.width)/(float)size.getX();
//		this.yScale = (float) (mc.currentScreen.height)/(float)size.getY();		
		
		//FIXME: test!
		this.xScale = mc.mainWindow.getScaledWidth();
		this.yScale = mc.mainWindow.getScaledHeight();
		
	}

	public void acceptKey(int key) {

		// Poll keyboard
//		InventoryConfig.acceptKeyPress(key);

		// Handle key press
		// First 5 tabs on top (not inc search which has it's own key already)
		if (key == InventoryConfig.key0.get()) {
			this.switchToTab(0);
		} else if (key == InventoryConfig.key1.get()) {
			this.switchToTab(1);
		} else if (key == InventoryConfig.key2.get()) {
			this.switchToTab(2);
		} else if (key == InventoryConfig.key3.get()) {
			this.switchToTab(3);
		} else if (key == InventoryConfig.key4.get()) {
			this.switchToTab(4);			
		} else if (key == InventoryConfig.keySearch.get()) {
			this.switchToTab(5);
		}
		// 5 tabs on bottom (not inc survival since it's unlikely you need it)
		// Note indices are offset by one since we skipped search.
		else if (key == InventoryConfig.key5.get()) {
			this.switchToTab(6);
		} else if (key == InventoryConfig.key6.get()) {
			this.switchToTab(7);
		} else if (key == InventoryConfig.key7.get()) {
			this.switchToTab(8);
		} else if (key == InventoryConfig.key8.get()) {
			this.switchToTab(9);
		} else if (key == InventoryConfig.key9.get()) {
			this.switchToTab(10);
		} else if (key == InventoryConfig.keyPrev.get()) {
			this.switchToTab(validateTabIdx(currTab - 1));
		} else if (key == InventoryConfig.keyNext.get()) {
			this.switchToTab(validateTabIdx(currTab + 1));
		} else if (key == InventoryConfig.keyNextItemRow.get()) {
			itemRow++;
			itemRow %= NUM_ROWS;
			// first position on a page starts at -1, -1
			itemCol = Math.max(itemCol, 0); 
			this.hoverItem();
		} else if (key == InventoryConfig.keyNextItemCol.get()) {
			itemCol++;
			itemCol %= NUM_COLS;
			// first position on a page starts at -1, -1
			itemRow = Math.max(itemRow, 0);
			this.hoverItem();
		} else if (key == InventoryConfig.keyDrop.get()) {
			this.switchToTab(-1);
		} else if (key == InventoryConfig.keyScrollUp.get()) {
			this.scrollDown(-2);
		} else if (key == InventoryConfig.keyScrollDown.get()) {
			this.scrollDown(+2);
		}
		
	}
	
	private int itemRow = -1;
	private int itemCol = -1;
	
	private void scrollDown(int amount) {
		// Make sure mouse is over window before scrolling
		this.moveMouseIntoWindow();
		
		// Warning: requesting more than 1 notch probably won't work,
		// minecraft seems to ignore multiple notch requests.
		// If you want to support more, you'll have to dig deeper into
		// minecraft.
		robot.mouseWheel(amount);	
	}
	
	private void hoverItem() {		
		int yPos = topItemYPos - itemRow*itemWidth;
		int xPos = leftItemXPos + itemCol*itemWidth;
		
//		org.lwjgl.input.Mouse.setCursorPosition((int)(xPos*this.xScale),
	//			(int)(yPos*this.yScale));
		GLFW.glfwSetCursorPos(Minecraft.getInstance().mainWindow.getHandle(), xPos, yPos);
		//FIXME: need scaling??
	}
	
	private void moveMouseIntoWindow() {
		// move mouse to location in window before e.g. mouse event
		int xPos = leftColXPos - tabWidth;
		int yPos = topRowYPos;
		
		//FIXME: need scaling??
		GLFW.glfwSetCursorPos(Minecraft.getInstance().mainWindow.getHandle(), xPos, yPos);
//		Mouse.setCursorPosition((int)(xPos*this.xScale),
//												(int)(yPos*this.yScale));

	}
	
	private void switchToTab(int iTab) {
		
		// Set up (x, y) for specified tab 
		int xPos = -1;
		int yPos = -1;
		switch(iTab) {
		case -1:
			// this is proxy for "drop by clicking outside inventory"
			xPos = leftColXPos - tabWidth;
			yPos = topRowYPos;
			break;
		case 0:
			xPos = leftColXPos;
			yPos = topRowYPos;
			break;
		case 1:
			xPos = leftColXPos+tabWidth;
			yPos = topRowYPos;
			break;
		case 2:
			xPos = leftColXPos+2*tabWidth;
			yPos = topRowYPos;
			break;
		case 3:
			xPos = leftColXPos+3*tabWidth;
			yPos = topRowYPos;
			break;
		case 4:
			xPos = leftColXPos+4*tabWidth;
			yPos = topRowYPos;
			break;
		case 5: 
			xPos = leftColXPos+6*tabWidth;
			yPos = topRowYPos;
			break;
		case 6:
			xPos = leftColXPos;
			yPos = bottomRowYPos;
			break;
		case 7:
			xPos = leftColXPos+tabWidth;;
			yPos = bottomRowYPos;
			break;
		case 8:
			xPos = leftColXPos+2*tabWidth;;
			yPos = bottomRowYPos;
			break;
		case 9:
			xPos = leftColXPos+3*tabWidth;;
			yPos = bottomRowYPos;
			break;
		case 10:
			xPos = leftColXPos+4*tabWidth;
			yPos = bottomRowYPos;
			break;			
		case 11:
			xPos = leftColXPos+6*tabWidth;
			yPos = bottomRowYPos;
			break;
		default:
			System.out.println("Unknown tab requested");
			break;
		}
		
		// Select the tab via a mouse action
		if (xPos > -1) {
			GLFW.glfwSetCursorPos(Minecraft.getInstance().mainWindow.getHandle(), xPos, yPos);			
			//FIXME scaled??
			
//			org.lwjgl.input.Mouse.setCursorPosition((int)(xPos*this.xScale),
//													(int)(yPos*this.yScale));
			// NB: We use lwjgl to move the mouse because it uses coordinates
			// relative to minecraft window. We use a java robot to click because
			// I don't know how else to.
			
			// IMPORTANT NB: If you do a separate press and release, while using eye gaze
			// mouse emulation, it's quite common for the mouse press to persist long 
			// enough that the mouse gets pressed *where the user is looking* in addition
			// to the inventory location. This causes minecraft to lose focus and eye mine
			// stops working. It seems to work fine just to send a mouseRelease event, but
			// a better solution would be good...
			//robot.mousePress(KeyEvent.BUTTON1_MASK);
			robot.mouseRelease(KeyEvent.BUTTON1_MASK);
			
			// we want to trigger 'tabChanged' if user has explicitly selected
			// the same tab again (otherwise this gets missed)
			this.onTabChanged();
		}	
	}
		
	// Ensure index in range, wrap if necessary
	private int validateTabIdx(int idx) {
		idx += NUM_TABS; // ensure positive
		idx %= NUM_TABS; // modulo into range	
		return idx;
	}
	
}