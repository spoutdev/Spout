#version 330

layout(location=0) in vec4 vPosition;
layout(location=1) in vec4 vColor;
layout(location=2) in vec4 vNormal;
layout(location=3) in vec2 vTexCoord;
layout(location=4) in vec2 bone_weights;
layout(location=5) in vec2 bone_ids;

out vec4 color;
out vec4 normal;
out vec2 uvcoord;

uniform mat4 Projection;
uniform mat4 View;
uniform mat4 Model;
uniform mat4[10] bone_matrix1;

void main() {
	vec4 bone_transform = vec4(0,0,0,0);

	for (int i=0 ; i<2 ; ++i) {
		bone_transform += bone_weights[i] * bone_matrix1[int(bone_ids[i])] * vPosition;
	}

	gl_Position = Projection * View * Model * bone_transform;

	uvcoord = vTexCoord;
	normal = vNormal;
	color = vColor;
}
