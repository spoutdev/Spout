#version 120
					
					
attribute vec4 vPosition;
attribute vec4 vColor;
attribute vec2 vTexCoord;

varying vec4 color;
varying vec2 uvcoord;
varying vec4 normal;

uniform mat4 Projection;
uniform mat4 View;
uniform mat4 Model;
		
void main()
{
	gl_Position = Projection * View  * Model * vPosition;
	
	uvcoord = vTexCoord;
	color = vColor;
	normal = View * Model * vPosition;
} 
