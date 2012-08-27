#version 330

in vec4 color;
in vec2 uvcoord;
uniform sampler2D tex;
uniform sampler2D font;
uniform vec4 fontcolor;


layout(location=0) out vec4 outputColor;

void main()
{
	vec4 fontsample = texture(font, uvcoord);
    outputColor = fontcolor * fontsample;
} 