#version 120

uniform sampler2D Diffuse;

varying vec4 color;
varying vec2 uvcoord;

void main()
{
	//gl_FragColor = color;
	gl_FragColor = texture2D(Diffuse, uvcoord);
} 