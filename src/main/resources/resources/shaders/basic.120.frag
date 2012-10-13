#version 120

varying vec4 color;
uniform sampler2D font;

void main()
{
    gl_FragColor = color;
} 