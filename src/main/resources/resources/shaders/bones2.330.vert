#version 330
  
layout(location=0) in vec4 vPosition;
layout(location=1) in vec4 vColor;
layout(location=2) in vec4 vNormal;
layout(location=3) in vec2 vTexCoord;
layout(location=4) in vec2 bone_weights;
layout(location=5) in vec2 bone_ids;

out vec4 color;
out vec2 uvcoord;

uniform mat4 Projection;
uniform mat4 View;
uniform mat4[2] bone_matrixes;
      
void main()
{
   vec4 bone_transform = vec4(0,0,0,0);
   
   float total = 0;
   for (int i=0 ; i<2 ; ++i) {
      bone_transform += bone_weights[i] * bone_matrixes[int(bone_ids[i])] * vec4(1,1,1,1);
      
      total += bone_weights[i];
   }
   bone_transform /= total;
   
   gl_Position = Projection * View * vPosition * bone_transform;
   
   uvcoord = vTexCoord;
   color = vColor;
}