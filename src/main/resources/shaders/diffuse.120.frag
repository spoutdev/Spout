#version 120

varying vec2 uvcoord;
uniform sampler2D Diffuse;

void main() {
	gl_FragColor = texture2D(Diffuse, uvcoord);
}
