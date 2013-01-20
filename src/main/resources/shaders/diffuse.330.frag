#version 330

in vec2 uvcoord;
uniform sampler2D Diffuse;

layout(location=0) out vec4 outputColor;

void main()
{
	outputColor = texture(Diffuse, uvcoord);
}