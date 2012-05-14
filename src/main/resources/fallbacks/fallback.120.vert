#version 120
					
					
attribute vec4 vPosition;
attribute vec4 vColor;
attribute vec2 vTexCoord;

varying vec4 color;
varying vec2 uvcoord;

uniform mat4 Projection;
uniform mat4 View;
		
void main()
{
	gl_Position = Projection * View * vPosition;
	
	uvcoord = vTexCoord;
	color = vColor;

} 
