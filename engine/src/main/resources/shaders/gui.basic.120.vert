#version 120

attribute vec4 vPosition;
attribute vec4 vColor;
attribute vec4 vNormal;
attribute vec2 vTexCoord;

varying vec4 color;
varying vec4 normal;
varying vec2 uvcoord;

uniform mat4 Projection;
uniform mat4 View;
uniform mat3 Model;

void main() {
	mat4 model = mat4(Model);
	model[3][0] = model[2][0];
	model[3][1] = model[2][1];
	model[3][2] = model[2][2];
	model[2][0] = 0.0;
	model[2][1] = 0.0;
	model[2][2] = 1.0;
	gl_Position = Projection * View * model * vPosition;
	
	uvcoord = vTexCoord;
	color = vColor;
	normal = vNormal;
}