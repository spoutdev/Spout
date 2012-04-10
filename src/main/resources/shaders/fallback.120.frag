 #version 120

varying vec4 color;
varying vec2 uvcoord;
uniform sampler2D texture;
									
void main()
{
	gl_FragColor =  color;
	//gl_FragColor = texture2D(texture, uvcoord);
} 