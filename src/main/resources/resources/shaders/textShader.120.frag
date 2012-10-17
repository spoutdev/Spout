#version 120

varying vec4 color;
varying vec2 uvcoord;
uniform sampler2D tex;
uniform sampler2D Diffuse;
uniform vec4 fontcolor;


void main()
{
    vec4 fontsample = texture(Diffuse, uvcoord);
    gl_FragColor = fontcolor * fontsample;
} 