package com.base.engine.rendering;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;
import java.util.HashMap;

import com.base.engine.components.*;
import com.base.engine.core.GameObject;
import com.base.engine.core.Transform;
import com.base.engine.core.Vector3f;
import com.base.engine.rendering.resourceManagement.MappedValues;

public class RenderingEngine extends MappedValues {
	
	private Camera mainCamera;
	
	private ArrayList<BaseLight> lights;
	private BaseLight activeLight;
	
	private HashMap<String, Integer> samplerMap;
	
	private Shader forwardAmbient;

	public RenderingEngine() {
		super();
		
		lights = new ArrayList<BaseLight>();
		samplerMap = new HashMap<String, Integer>();
		samplerMap.put("diffuse", 0);
		
		addVector3f("ambient", new Vector3f(.1f, .1f, .1f));
		
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		
		glFrontFace(GL_CW);
		glCullFace(GL_BACK);
		glEnable(GL_CULL_FACE);
		glEnable(GL_DEPTH_TEST);
		
		// TODO: depth clamp for later
		
		// glEnable(GL_DEPTH_CLAMP);
		glEnable(GL_TEXTURE_2D);
		// glEnable(GL_FRAMEBUFFER_SRGB);
		
		/* mainCamera = new Camera((float) Math.toRadians(70.0f),
				(float) Window.getWidth() / (float) Window.getHeight(),
				0.01f, 1000.0f); */
		
		forwardAmbient = new Shader("forward-ambient");
	}

	public void render(GameObject object) {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		lights.clear();
		object.addToRenderingEngine(this);
		
		object.render(forwardAmbient, this);
		
		glEnable(GL_BLEND);
		glBlendFunc(GL_ONE, GL_ONE);
		glDepthMask(false);
		glDepthFunc(GL_EQUAL);
		
		for (BaseLight light : lights) {
			activeLight = light;
			object.render(light.getShader(), this);
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

	public Camera getMainCamera() {
		return mainCamera;
	}

	public void setMainCamera(Camera mainCamera) {
		this.mainCamera = mainCamera;
	}

	public void addCamera(Camera camera) {
		mainCamera = camera;
	}

	public int getSamplerSlot(String samplerName) {
		return samplerMap.get(samplerName);
	}
	
	public void updateUniformStruct(Transform transform, Material material,
			Shader shader, String uniformName, String uniformType) {
		throw new IllegalArgumentException(uniformName + "," +
				uniformType + " for rendering engine ");
	}
}
