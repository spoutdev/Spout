#version 330

layout(location=0) in vec4 vPosition;
layout(location=1) in vec4 vColor;
layout(location=2) in vec4 vNormal;
layout(location=3) in vec2 vTexCoord;
layout(location=4) in float bone_ids;

out vec4 color;
out vec4 normal;
out vec2 uvcoord;

uniform mat4 Projection;
uniform mat4 View;
uniform mat4 Model;
uniform mat4[6] bone_matrix1;

void main() {
	int boneId = int(bone_ids);
	gl_Position = Projection * View * Model * bone_matrix1[boneId] * vPosition;
	color = vColor;
	uvcoord = vTexCoord;
	normal = vNormal;
}
