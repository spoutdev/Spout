#version 330
					
					
layout(location=0) in vec4 vPosition;
layout(location=1) in vec4 vColor;
layout(location=2) in vec4 vNormal;
layout(location=3) in vec2 vTexCoord;

out vec4 color;
out vec2 uvcoord;

uniform mat4 Projection;
uniform mat4 View;
uniform mat4 Model;
		
void main()
{
	gl_Position = Projection * View * Model * vPosition;
	
	uvcoord = vTexCoord;
	color = vColor;

} 
