#version 330

in vec2 uvcoord;
in vec4 color;
in vec4 normal;

uniform sampler2D Diffuse;
uniform vec4 BlendColor;

layout(location=0) out vec4 outputColor;
layout(location=1) out vec4 normals;

void main() {
	outputColor = texture(Diffuse, uvcoord) * color * BlendColor;
	normals =  (normal + vec4(1, 1, 1, 1)) / 2;
}
