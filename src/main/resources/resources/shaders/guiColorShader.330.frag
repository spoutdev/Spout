#version 330

in vec4 color;

uniform vec4 BlendColor;

layout(location=0) out vec4 outputColor;

void main()
{
	outputColor = vec4(color.rgb * BlendColor.rgb, 1);
} 