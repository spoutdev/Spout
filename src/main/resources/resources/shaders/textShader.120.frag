#version 120

varying vec4 color;
varying vec2 uvcoord;
uniform sampler2D tex;
uniform sampler2D Diffuse;


void main()
{
    vec4 fontsample = texture(Diffuse, uvcoord);
    gl_FragColor = fontsample * color;
} 