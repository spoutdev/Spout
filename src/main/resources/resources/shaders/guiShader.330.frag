#version 330

in vec2 uvcoord;
in vec4 color;

uniform sampler2D Diffuse;
uniform vec4 BlendColor;

layout(location=0) out vec4 outputColor;

void main()
{
	vec4 t = texture(Diffuse, uvcoord);
	
	outputColor = t * vec4(color.rgb * BlendColor.rgb, 1);
} 