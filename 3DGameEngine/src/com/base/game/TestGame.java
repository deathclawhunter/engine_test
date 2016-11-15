package com.base.game;

import com.base.engine.core.Game;
import com.base.engine.core.GameObject;
import com.base.engine.core.Transform;
import com.base.engine.core.Vector2f;
import com.base.engine.core.Vector3f;
import com.base.engine.rendering.Camera;
import com.base.engine.rendering.Material;
import com.base.engine.rendering.Mesh;
import com.base.engine.rendering.Texture;
import com.base.engine.rendering.Vertex;
import com.base.engine.rendering.Window;

public class TestGame extends Game {
		
	private Camera camera;
	private GameObject planeObject;
	
	@Override
	public void init() {
		camera = new Camera();
		
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
		planeObject.getTransform().setTranslation(0, -1, 5);
		getRootObject().addChild(planeObject);
		 
		Transform.setProjection(70f, Window.getWidth(), Window.getHeight(),
				0.1f, 1000);
		Transform.setCamera(camera);
	}
}
