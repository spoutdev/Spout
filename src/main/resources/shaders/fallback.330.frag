 #version 330

in vec4 color;
in vec2 uvcoord;
uniform sampler2D texture;

layout(location=0) out vec4 outputColor;
									
void main()
{
	outputColor =  color;
	//outputColor = texture(texture, uvcoord);
} 