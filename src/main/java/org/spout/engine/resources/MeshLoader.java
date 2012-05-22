package org.spout.engine.resources;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

import org.spout.api.math.Vector2;
import org.spout.api.math.Vector3;
import org.spout.api.model.ModelFace;
import org.spout.api.model.PositionNormalTexture;
import org.spout.engine.filesystem.BasicResourceLoader;
import org.spout.engine.mesh.BaseMesh;


public class MeshLoader extends BasicResourceLoader<BaseMesh> {

	
	private static BaseMesh LoadObj(InputStream stream){
		Scanner scan = new Scanner(stream);
		
		ArrayList<Vector3> verticies = new ArrayList<Vector3>();
		ArrayList<Vector3> normals = new ArrayList<Vector3>();
		ArrayList<Vector2> uvs = new ArrayList<Vector2>();
		ArrayList<ModelFace> faces = new ArrayList<ModelFace>();
		
		while(scan.hasNext()){
			String s = scan.nextLine();
			if(s.startsWith("#")) continue; //it's a comment, skip it
			if(s.startsWith("v")){
				String[] sp = s.split(" ");
				verticies.add(new Vector3(Float.parseFloat(sp[1]), Float.parseFloat(sp[2]), Float.parseFloat(sp[3])));
			}
			if(s.startsWith("vn")){
				String[] sp = s.split(" ");
				normals.add(new Vector3(Float.parseFloat(sp[1]), Float.parseFloat(sp[2]), Float.parseFloat(sp[3])));
			}
			if(s.startsWith("vt")){
				String[] sp = s.split(" ");
				uvs.add(new Vector2(Float.parseFloat(sp[1]), Float.parseFloat(sp[2])));
			}
			if(s.startsWith("f")){
				String[] sp = s.split(" ");
				
				if(sp[1].contains("//")){
					ArrayList<PositionNormalTexture> ar = new ArrayList<PositionNormalTexture>();
					for(int i = 1; i <= 3; i++){
						String[] sn = sp[i].split("//");
						int pos = Integer.parseInt(sn[0]);
						int norm = Integer.parseInt(sn[1]);
						ar.add(new PositionNormalTexture(verticies.get(pos - 1), normals.get(norm -1)));
											
					}
					faces.add(new ModelFace(ar.get(0), ar.get(1), ar.get(2)));
					ar.clear();
					
				} else if(sp[1].contains("/")){
					ArrayList<PositionNormalTexture> ar = new ArrayList<PositionNormalTexture>();
					for(int i = 1; i <= 3; i++){
						String[] sn = sp[i].split("/");
						int pos = Integer.parseInt(sn[0]);
						int uv = Integer.parseInt(sn[1]);
						ar.add(new PositionNormalTexture(verticies.get(pos - 1), uvs.get(uv -1)));
											
					}
					faces.add(new ModelFace(ar.get(0), ar.get(1), ar.get(2)));
					ar.clear();
					
					
				} else {
					int face1 = Integer.parseInt(sp[1]) - 1;
					int face2 = Integer.parseInt(sp[2]) - 1;
					int face3 = Integer.parseInt(sp[3]) - 1;
					
					PositionNormalTexture p = new PositionNormalTexture(verticies.get(face1));
					PositionNormalTexture p2 = new PositionNormalTexture(verticies.get(face2));
					PositionNormalTexture p3 = new PositionNormalTexture(verticies.get(face3));
					
					faces.add(new ModelFace(p, p2, p3));
					
					
				}
				
				
			
				
			}
				
		}
		
		return new BaseMesh(faces);
		
	}
	
	
	@Override
	public String getFallbackResourceName() {
		return "mesh://Spout/fallbacks/fallback.obj";
	}

	@Override
	public BaseMesh getResource(InputStream stream) {
		return MeshLoader.LoadObj(stream);
	}

}
