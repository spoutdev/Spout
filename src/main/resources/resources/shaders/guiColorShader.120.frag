#version 120

varying vec4 color;
							
uniform vec4 BlendColor;	
						
void main()
{	
	gl_FragColor = color * BlendColor;
} 