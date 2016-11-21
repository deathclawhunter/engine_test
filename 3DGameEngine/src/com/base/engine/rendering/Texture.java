package com.base.engine.rendering;

import static org.lwjgl.opengl.GL11.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.HashMap;

import javax.imageio.ImageIO;

// import org.newdawn.slick.opengl.TextureLoader;

import com.base.engine.core.Util;
import com.base.engine.rendering.resourceManagement.TextureResource;

public class Texture {
	
	private String fileName;
	private TextureResource resource;
	private static HashMap<String, TextureResource> loadedTextures = new HashMap<String, TextureResource>();
	
	public Texture(String fileName) {
		
		this.fileName = fileName;
		TextureResource oldResource = loadedTextures.get(fileName);
		if (oldResource != null) {
			resource = oldResource;
			resource.addReference();
		} else {
			resource = new TextureResource(loadTexture(fileName));
			loadedTextures.put(fileName, resource);
		}
	}
	
	public void bind() {
		glBindTexture(GL_TEXTURE_2D, resource.getId());
	}
	
	public int getID() {
		return resource.getId();
	}
	
	@Override
	protected void finalize() {
		if (resource.removeReference() && !fileName.isEmpty()) {
			loadedTextures.remove(fileName);
		}
	}
	
	private static int loadTexture(String fileName) {
		
		String[] splitArray = fileName.split("\\.");
		String ext = splitArray[splitArray.length - 1]; 
		
		try {
			// int id = 0; // TextureLoader.getTexture(ext, new FileInputStream(new File("./res/textures/" + fileName))).getTextureID();
			
			BufferedImage image = ImageIO.read(new File("./res/textures/" + fileName));
			int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(),
					null, 0, image.getWidth());
			
			ByteBuffer buffer = Util.createByteBuffer(image.getHeight() * image.getWidth() * 4);
			boolean hasAlpha = image.getColorModel().hasAlpha();
			
			for (int y = 0; y < image.getHeight(); y++) {
				for (int x = 0; x < image.getWidth(); x++) {
					int pixel = pixels[y * image.getWidth() + x];
					
					buffer.put((byte) ((pixel >> 16) & 0x0FF));
					buffer.put((byte) ((pixel >> 8) & 0x0FF));
					buffer.put((byte) ((pixel) & 0x0FF));
					
					if (hasAlpha) {
						buffer.put((byte) ((pixel >> 24) & 0x0FF));
					} else {
						buffer.put((byte) 0x0FF);
					}
				}
			}
			
			buffer.flip();
			
			int id = glGenTextures();
			
			glBindTexture(GL_TEXTURE_2D, id);
			
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
			
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(),
					image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
			
			return id;
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		return 0;
	}
}
