#version 330

in vec4 color;
in vec2 uvcoord;
uniform sampler2D Diffuse;
uniform vec4 fontcolor;


layout(location=0) out vec4 outputColor;

void main()
{
	vec4 fontsample = texture(Diffuse, uvcoord);
    outputColor = fontcolor * fontsample;
} 