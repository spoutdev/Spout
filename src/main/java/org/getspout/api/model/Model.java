package org.getspout.api.model;

public class Model {
	BoneTransform modelRoot = new BoneTransform();
	Bone root = new Bone("root", modelRoot);
   
	/**
	 * Attaches a Mesh to this model at the provided Bone name
	 * @param attachTo
	 * @param boneName
	 * @param mesh
	 */
	void attachMesh(String attachTo, String boneName, Mesh mesh){
		Bone bone = root.getBone(attachTo);
		if(bone == null) throw new BoneNotFoundException("Bone " + attachTo + " Not found");
		bone.attachBone(boneName, mesh);
	
	}
	/**
	 * Attaches a mesh to the Root bone.  
	 * @param boneName
	 * @param mesh
	 */
	void attachMesh(String boneName, Mesh mesh){
		this.attachMesh("root", boneName, mesh);
	}
	
	
}
