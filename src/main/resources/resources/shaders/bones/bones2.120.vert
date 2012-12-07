#version 120

attribute vec4 vPosition;
attribute vec4 vColor;
attribute vec4 vNormal;
attribute vec2 vTexCoord;
attribute vec2 bone_weights;
attribute vec2 bone_ids;

varying vec4 color;
varying vec4 normal;
varying vec2 uvcoord;

uniform mat4 Projection;
uniform mat4 View;
uniform mat4 Model;
uniform mat4[10] bone_matrix;

void main()
{
   vec4 bone_transform = vec4(0,0,0,0);
   
   float total = 0;
   for (int i=0 ; i<2 ; ++i) {
      bone_transform += bone_weights[i] * bone_matrix[int(bone_ids[i])] * vec4(1,1,1,1);
      
      total += bone_weights[i];
   }
   bone_transform /= total;
   
   gl_Position = Projection * View * vPosition * bone_transform;
   
   uvcoord = vTexCoord;
   color = vColor;
}