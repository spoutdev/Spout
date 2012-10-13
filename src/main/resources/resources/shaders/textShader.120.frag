#version 120

varying vec4 color;
varying vec2 uvcoord;
uniform sampler2D tex;
uniform sampler2D font;
uniform vec4 fontcolor;


void main()
{
    vec4 fontsample = texture(font, uvcoord);
    gl_FragColor = fontcolor * fontsample;
    gl_FragColor = color;
} 