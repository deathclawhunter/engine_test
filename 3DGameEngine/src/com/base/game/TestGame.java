package com.base.game;

import com.base.engine.components.DirectionalLight;
import com.base.engine.components.MeshRenderer;
import com.base.engine.components.PointLight;
import com.base.engine.components.SpotLight;
import com.base.engine.core.Game;
import com.base.engine.core.GameObject;
import com.base.engine.core.Vector2f;
import com.base.engine.core.Vector3f;
import com.base.engine.rendering.Material;
import com.base.engine.rendering.Mesh;
import com.base.engine.rendering.Texture;
import com.base.engine.rendering.Vertex;

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
		
		Mesh mesh = new Mesh(vertices, indices, true);
		Material material = new Material(new Texture("test.png"),
				new Vector3f(1, 1, 1), 1, 8);
		MeshRenderer meshRenderer = new MeshRenderer(mesh, material);
		
		planeObject = new GameObject();
		planeObject.addComponent(meshRenderer);
		planeObject.getTransform().getPos().set(0, -1, 5);
		
		GameObject directionalLightObject = new GameObject();
		DirectionalLight directionalLight = new DirectionalLight(new Vector3f(0f, 0f, 1f), 0.4f,
				new Vector3f(1, 1, 1));
		directionalLightObject.addComponent(directionalLight);
		
		GameObject pointLightObject = new GameObject();
		pointLightObject.addComponent(new PointLight(new Vector3f(0f, 1f, 0f), 0.4f,
				new Vector3f(0, 0, 1)));
		
		SpotLight spotLight = new SpotLight(new Vector3f(0f, 1f, 1f), 0.8f,
				new Vector3f(0, 0, .05f), new Vector3f(1, 0, 0), 0.7f);
		GameObject spotLightObject = new GameObject();
		spotLightObject.addComponent(spotLight);
		
		spotLight.getTransform().setPos(new Vector3f(5, 0, 5));
		
		getRootObject().addChild(planeObject);
		getRootObject().addChild(directionalLightObject);
		getRootObject().addChild(pointLightObject);
		getRootObject().addChild(spotLightObject);
	}
}
