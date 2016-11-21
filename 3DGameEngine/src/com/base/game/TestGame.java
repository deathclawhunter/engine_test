package com.base.game;

import com.base.engine.components.Camera;
import com.base.engine.components.DirectionalLight;
import com.base.engine.components.MeshRenderer;
import com.base.engine.components.PointLight;
import com.base.engine.components.SpotLight;
import com.base.engine.core.Game;
import com.base.engine.core.GameObject;
import com.base.engine.core.Quaternion;
import com.base.engine.core.Vector2f;
import com.base.engine.core.Vector3f;
import com.base.engine.rendering.Material;
import com.base.engine.rendering.Mesh;
import com.base.engine.rendering.Texture;
import com.base.engine.rendering.Vertex;
import com.base.engine.rendering.Window;

public class TestGame extends Game {
		
	private GameObject planeObject;
	
	@Override
	public void init() {
		
		float fieldDepth = 10.0f;
		float fieldWidth = 10.0f;
		
		Vertex[] vertices = new Vertex[] {
				new Vertex(new Vector3f(-fieldWidth, 0.0f, -fieldDepth), new Vector2f(0, 0)),
				new Vertex(new Vector3f(-fieldWidth, 0.0f, fieldDepth * 3), new Vector2f(0, 1f)),
				new Vertex(new Vector3f(fieldWidth * 3, 0.0f, -fieldDepth), new Vector2f(1.0f, 0)),
				new Vertex(new Vector3f(fieldWidth * 3, 0.0f, fieldDepth * 3), new Vector2f(1f, 1f))};
		
		int[] indices = new int[] {0, 1, 2,
									2, 1, 3};
		
		Vertex[] vertices2 = new Vertex[] {
				new Vertex(new Vector3f(-fieldWidth / 10, 0.0f, -fieldDepth / 10), new Vector2f(0, 0)),
				new Vertex(new Vector3f(-fieldWidth / 10, 0.0f, fieldDepth / 10 * 3), new Vector2f(0, 1f)),
				new Vertex(new Vector3f(fieldWidth / 10 * 3, 0.0f, -fieldDepth / 10), new Vector2f(1.0f, 0)),
				new Vertex(new Vector3f(fieldWidth / 10 * 3, 0.0f, fieldDepth / 10 * 3), new Vector2f(1f, 1f))};
		
		int[] indices2 = new int[] {0, 1, 2,
									2, 1, 3};
		
		Mesh mesh = new Mesh(vertices, indices, true);
		Mesh mesh2 = new Mesh(vertices2, indices2, true);
		Material material = new Material(); // , new Vector3f(1, 1, 1), 1, 8
		material.addTexture("diffuse", new Texture("test.png"));
		material.addFloat("specularIntensity", 1f);
		material.addFloat("specularPower", 8f);
		
		Material material2 = new Material(); // , new Vector3f(1, 1, 1), 1, 8
		material2.addTexture("diffuse", new Texture("bricks.jpg"));
		material2.addFloat("specularIntensity", 1f);
		material2.addFloat("specularPower", 8f);
		
		Mesh tempMesh = new Mesh("MonkeyHead.obj");
		
		MeshRenderer meshRenderer = new MeshRenderer(mesh, material);
		
		planeObject = new GameObject();
		planeObject.addComponent(meshRenderer);
		planeObject.getTransform().getPos().set(0, -1, 5);
		
		GameObject directionalLightObject = new GameObject();
		DirectionalLight directionalLight = new DirectionalLight(new Vector3f(0f, 0f, 1f), 0.4f);
		directionalLightObject.addComponent(directionalLight);
		directionalLight.getTransform().setRot(new Quaternion(new Vector3f(1, 0, 0),
				(float) Math.toRadians(-45)));
		
		GameObject pointLightObject = new GameObject();
		pointLightObject.addComponent(new PointLight(new Vector3f(0f, 1f, 0f), 0.4f,
				new Vector3f(0, 0, 1)));
		
		SpotLight spotLight = new SpotLight(new Vector3f(0f, 1f, 1f), 0.8f,
				new Vector3f(0, 0, .05f), 0.7f);
		GameObject spotLightObject = new GameObject();
		spotLightObject.addComponent(spotLight);
		
		spotLight.getTransform().setPos(new Vector3f(5, 0, 5));
		spotLight.getTransform().setRot(new Quaternion(new Vector3f(0, 1, 0),
				(float) Math.toRadians(90.0f)));
		
		addObject(planeObject);
		addObject(directionalLightObject);
		addObject(pointLightObject);
		addObject(spotLightObject);
		
		GameObject testMesh1 = new GameObject().addComponent(new MeshRenderer(mesh2, material)); 
		GameObject testMesh2 = new GameObject().addComponent(new MeshRenderer(mesh2, material));
		GameObject testMesh3 = new GameObject().addComponent(new MeshRenderer(tempMesh, material));
		
		testMesh1.getTransform().getPos().set(0,  2,  0);
		testMesh1.getTransform().setRot(new Quaternion(new Vector3f(0, 1, 0), 0.4f));
		testMesh2.getTransform().getPos().set(0,  0,  5);
		
		testMesh1.addChild(testMesh2);
		
		testMesh2.addChild(new GameObject().addComponent(new Camera((float) Math.toRadians(70.0f),
				(float) Window.getWidth() / (float) Window.getHeight(),
				0.01f, 1000.0f)));
		
		addObject(testMesh1);
		addObject(testMesh3);
		
		testMesh3.getTransform().getPos().set(5, 5, 5);
		testMesh3.getTransform().setRot(new Quaternion(new Vector3f(0, 1, 0), (float) Math.toRadians(-70.0f)));
		
		addObject(new GameObject().addComponent(new MeshRenderer(new Mesh("MonkeyHead.obj"), material2)));
	}
}
