#version 330
//SSAO Post Process Shader

uniform sampler2D scene_depth;
uniform sampler2D scene_normal;
uniform sampler2D noise;

uniform int kernelSize;
uniform vec3[] kernel;

uniform float radius;

uniform vec2 noiseScale;

uniform mat4 Projection;

in vec3 view_ray;
in vec2 uvcoord;


layout(location=0) out vec4 outputColor;

void main() {

	//Reconstruct the position of the fragment from the depth
	vec3 origin = view_ray * texture(scene_depth, uvcoord).r;
	
	//Get the fragment's normal;
	vec3 normal  = texture(scene_normal, uvcoord).xyz * 2.0 - 1.0;
	normal = normalize(normal);
	
	//Construct a change of basis matrix to reorient our sample kernel along the object's normal.
	
	//Extract the random vector from the noise texture
	vec3 rvec = texture(noise, uvcoord * noiseScale).xyz * 2.0 - 1.0;
	
	//Calculate the tangent and bitangent using gram-shmidt.  
	vec3 tangent = normalize(rvec - normal * dot(rvec, normal));
	vec3 bitangent = cross(normal, tangent);
	
	mat3 tbn = mat3(tangent, bitangent, normal);	
	
	float occlusion = 0.0;
	
	for(int i = 0; i < kernelSize; i++) {
		//Get the sample position
		vec3 sample = tbn * kernel[i];
		sample = sample * radius + origin;
	
		//Project the sample
		vec4 offset = vec4(sample, 1.0);
		offset = Projection * offset;
		offset.xy /= offset.w;
		offset.xy = offset.xy * 0.5 + 0.5
		
		//Get the sample depth
		float sample_depth = texture(scene_depth, offset.xy).r;
		
		//Range check and accumulate
		float range_check = abs(origin.z - sample_depth) < radius ? 1.0 : 0.0;
		
		occlusion += (sample_depth <= sample.z ? 1.0 : 0.0) * range_check;
	}
		


}