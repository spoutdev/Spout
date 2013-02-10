#version 120

attribute vec4 vPosition;
attribute vec4 vColor;
attribute vec4 vNormal;
attribute vec2 vTexCoord;
attribute float bone_ids;

varying vec4 color;
varying vec4 normal;
varying vec2 uvcoord;

uniform mat4 Projection;
uniform mat4 View;
uniform mat4 Model;
uniform mat4[6] bone_matrix1;

void main() {
	int boneId = int(bone_ids);
	gl_Position = Projection * View * Model * bone_matrix1[boneId] * vPosition;

	uvcoord = vTexCoord;
	color = vColor;
	normal = vNormal;
}
