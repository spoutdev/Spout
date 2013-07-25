#version 330
//SSAO Vertex shader.  Passes along the view vector.

layout(location=0) in vec4 vPosition;
layout(location=1) in vec4 vColor;
layout(location=2) in vec4 vNormal;
layout(location=3) in vec2 vTexCoord;

out vec4 color;
out vec4 normal;
out vec2 uvcoord;
out vec3 view_ray;

uniform mat4 Projection;
uniform mat4 View;
uniform mat4 Model;

void main()
{
	gl_Position = Projection * View  * Model * vPosition;

	view_ray = (View * vec4(1, 0, 0, 0)).xyz;
	uvcoord = vTexCoord;
	color = vColor;
	normal = vNormal;
}