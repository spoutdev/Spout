#version 330

layout(location=0) in vec4 vPosition;
layout(location=1) in vec4 vColor;
layout(location=2) in vec4 vNormal;
layout(location=3) in vec2 vTexCoord;

out vec4 color;
out vec4 normal;
out vec2 uvcoord;

uniform mat4 Projection;
uniform mat4 View;
uniform mat3 Model;

void main() {
	mat4 model = mat4(Model);
	model[3][0] = model[2][0];
	model[3][1] = model[2][1];
	model[3][2] = model[2][2];
	model[2][0] = 0;
	model[2][1] = 0;
	model[2][2] = 1;
	gl_Position = Projection * View * mat4(model) * vPosition;
	
	uvcoord = vTexCoord;
	color = vColor;
	normal = vNormal;
}