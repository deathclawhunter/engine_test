package com.base.engine.core;

import com.base.engine.rendering.RenderingEngine;
import com.base.engine.rendering.Window;

public class CoreEngine {
	
	private boolean isRunning;
	private Game game;
	private int width, height;
	private double frameTime;
	private RenderingEngine renderingEngine;
	
	public CoreEngine(int width, int height, double frameRate, Game game) {
		this.isRunning = false;
		this.game = game;
		this.width = width;
		this.height = height;
		this.frameTime = 1.0d / frameRate;
		game.setEngine(this);
	}
	
	public void createWindow(String title) {
		Window.createWindow(width, height, title);
		renderingEngine = new RenderingEngine();
	}
	
	public void start() {
		
		if (isRunning) {
			return;
		}
		run();
	}
	
	public void stop() {
		if (!isRunning) {
			return;
		}
		isRunning = false;
	}
	
	public void run() {
		
		isRunning = true;
		
		int frames = 0;
		long frameCounter = 0;
		
		game.init();
		
		final double frameTime = this.frameTime;
		double lastTime = Time.getTime();
		double unprocessedTime = 0;
		
		
		while (isRunning) {
			boolean render = false;
			double startTime = Time.getTime();
			double passedTime = startTime - lastTime;
			lastTime = startTime;
			
			unprocessedTime += passedTime;
			frameCounter += passedTime;
			
			while (unprocessedTime > frameTime) {
				render = true;
				unprocessedTime -= frameTime;
				if (Window.isCloseRequested()) {
					stop();
				}
				
				game.input((float) frameTime);
				Input.update();
				game.update((float) frameTime);
				
				if (frameCounter >= 1) {
					System.out.println(frames);
					frames = 0;
					frameCounter = 0;
				}
			}
			
			if (render) {
				game.render(renderingEngine);
				frames++;
				// renderingEngine.render(game.getRootObject());
				Window.render();
			} else {
				try {
					Thread.sleep(1);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		cleanUp();
	}
	
	public void cleanUp() {
		Window.dispose();
	}

	public RenderingEngine getRenderingEngine() {
		return renderingEngine;
	}
}
