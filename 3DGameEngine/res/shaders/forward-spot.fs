#version 120

varying vec2 texCoord0;
varying vec3 normal0;
varying vec3 worldPos0;

uniform sampler2D diffuse;

uniform vec3 eyePos;
uniform float specularIntensity;
uniform float specularPower;

struct BaseLight {
	vec3 color;
	float intensity;
};

// how fast the point light fades off
struct Attenuation {
	float constant;
	float linear;
	float exponent;
};

struct PointLight {
	BaseLight base;
	Attenuation atten;
	vec3 position;
	float range;
};

struct SpotLight {
	PointLight pointLight;
	vec3 direction;
	float cutoff;
};

uniform SpotLight spotLight;

vec4 calcLight(BaseLight base, vec3 direction, vec3 normal) {
	float diffuseFactor = dot(normal, -direction);
	vec4 diffuseColor = vec4(0, 0, 0, 0);
	vec4 specularColor = vec4(0, 0, 0, 0);
	if (diffuseFactor > 0) {
		diffuseColor = vec4(base.color, 1.0) * base.intensity * diffuseFactor;
		
		vec3 directionToEye = normalize(eyePos - worldPos0);
		vec3 reflectDirection = normalize(reflect(direction, normal));
		
		float specularFactor = dot(directionToEye, reflectDirection);
		specularFactor = pow(specularFactor, specularPower);
		
		if (specularFactor > 0) {
			specularColor = vec4(base.color, 1.0) * specularIntensity * specularFactor;
		}
	}
	
	return diffuseColor + specularColor;
}

vec4 calcPointLight(PointLight pointLight, vec3 normal) {
	vec3 lightDirection = worldPos0 - pointLight.position;
	float distancToPoint = length(lightDirection);
	
	// optimization by range
	if (distancToPoint > pointLight.range) {
		return vec4(0, 0, 0, 0);
	}
	
	lightDirection = normalize(lightDirection);
	
	vec4 color = calcLight(pointLight.base, lightDirection, normal);
	
	float attenuation = pointLight.atten.constant +
		pointLight.atten.linear * distancToPoint +
		pointLight.atten.exponent * distancToPoint * distancToPoint +
		0.0001; // avoid divide by zero, opengl sometimes calculate
		// both code routes no matter we add if...else or not
		
	
	return color / attenuation;
}

vec4 calcSpotLight(SpotLight spotLight, vec3 normal) {
	 vec3 lightDirection = normalize(worldPos0 - spotLight.pointLight.position);
	 float spotFactor = dot(lightDirection, spotLight.direction);
	 vec4 color = vec4(0, 0, 0, 0);
	 
	 if (spotFactor > spotLight.cutoff) {
		color = calcPointLight(spotLight.pointLight, normal) * 
				(1.0 - (1.0 - spotFactor) / (1.0 - spotLight.cutoff));
	 }
	 
	 return color;
}

void main() {
	
	gl_FragColor = texture2D(diffuse, texCoord0.xy) *
			calcSpotLight(spotLight, normalize(normal0));
}