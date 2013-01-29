#version 120

attribute vec4 vPosition;
attribute vec4 vColor;
attribute vec4 vNormal;
attribute vec2 vTexCoord;
attribute vec2 bone_weights;
attribute vec2 bone_ids;
attribute float index;

varying vec4 color;
varying vec4 normal;
varying vec2 uvcoord;

uniform mat4[2] Projection;
uniform mat4[2] View;
uniform mat4[2] Model;
uniform mat4[20] bone_matrix1;
uniform mat4[20] bone_matrix2;

void main() {
	vec4 bone_transform = vec4(0,0,0,0);
	int instance = int(index);

	for (int i=0 ; i<2 ; ++i) {
		int bone_id = int(bone_ids[i]);
		bone_transform += bone_weights[i] * bone_matrix1[instance * 10 + bone_id] * bone_matrix2[instance * 10 + bone_id] * vPosition;
	}

	gl_Position = Projection[instance] * View[instance] * Model[instance] * bone_transform;

	uvcoord = vTexCoord;
	color = vColor;
}
