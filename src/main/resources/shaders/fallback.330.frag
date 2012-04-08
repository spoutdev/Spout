 #version 330

in vec4 color;
in vec2 uvcoord;
uniform sampler2D texture;
									
void main()
{
	gl_FragColor =  color;
} 