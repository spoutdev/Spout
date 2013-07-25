#version 330

layout(location=0) in vec4 vPosition;
layout(location=1) in vec4 vColor;
layout(location=2) in vec4 vNormal;
layout(location=3) in vec2 vTexCoord;
layout(location=4) in vec2 bone_weights;
layout(location=5) in vec2 bone_ids;
layout(location=6) in float index;

out vec4 color;
out vec4 normal;
out vec2 uvcoord;

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
	normal = vNormal;
	color = vColor;
}
