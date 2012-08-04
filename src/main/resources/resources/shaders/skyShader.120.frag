#version 120

varying vec4 color;
varying vec2 uvcoord;
uniform sampler2D Diffuse;
							
uniform vec4 BlendColor;							
void main()
{	
	gl_FragColor = texture2D(Diffuse, uvcoord) * color * BlendColor;
} 