package com.base.engine.rendering;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;

import com.base.engine.components.*;
import com.base.engine.core.GameObject;
import com.base.engine.core.Vector3f;

public class RenderingEngine {
	
	private Camera mainCamera;
	private Vector3f ambientLight;
	
	private ArrayList<BaseLight> lights;
	private BaseLight activeLight;

	public RenderingEngine() {
		lights = new ArrayList<BaseLight>();
		
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		
		glFrontFace(GL_CW);
		glCullFace(GL_BACK);
		glEnable(GL_CULL_FACE);
		glEnable(GL_DEPTH_TEST);
		
		// TODO: depth clamp for later
		
		// glEnable(GL_DEPTH_CLAMP);
		glEnable(GL_TEXTURE_2D);
		// glEnable(GL_FRAMEBUFFER_SRGB);
		
		mainCamera = new Camera((float) Math.toRadians(70.0f),
				(float) Window.getWidth() / (float) Window.getHeight(),
				0.01f, 1000.0f);
		
		ambientLight = new Vector3f(0.1f, 0.1f, 0.1f);
	}
	
	public Vector3f getAmbientLight() {
		return ambientLight;
	}
	
	public void input(float delta) {
		mainCamera.input(delta);
	}

	public void render(GameObject object) {
		clearScreen();
		
		lights.clear();
		object.addToRenderingEngine(this);
		
		Shader forwardAmbient = ForwardAmbient.getInstance();
		forwardAmbient.setRenderingEngine(this);
		
		object.render(forwardAmbient);
		
		glEnable(GL_BLEND);
		glBlendFunc(GL_ONE, GL_ONE);
		glDepthMask(false);
		glDepthFunc(GL_EQUAL);
		
		for (BaseLight light : lights) {
			light.getShader().setRenderingEngine(this);
			activeLight = light;
			object.render(light.getShader());
		}
		
		glDepthFunc(GL_LESS);
		glDepthMask(true);
		glDisable(GL_BLEND);
	}
	
	public void addLight(BaseLight light) {
		lights.add(light);
	}
	
	public BaseLight getActiveLight() {
		return activeLight;
	}
	
	public static void clearScreen() {
		// TODO: Stencil Buffer
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}
	
	public static void setTextures(boolean enabled) {
		if (enabled) {
			glEnable(GL_TEXTURE_2D);
		} else {
			glDisable(GL_TEXTURE_2D);
		}
	}
	
	public static String getOpenGLVersion() {
		return glGetString(GL_VERSION);
	}
	
	private static void setClearColor(Vector3f color) {
		glClearColor(color.getX(), color.getY(), color.getZ(), 1.0f);
	}

	private static void unbindTextures() {
		glBindTexture(GL_TEXTURE_2D, 0);
	}

	public Camera getMainCamera() {
		return mainCamera;
	}

	public void setMainCamera(Camera mainCamera) {
		this.mainCamera = mainCamera;
	}
}
