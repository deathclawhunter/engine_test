#version 330

const int MAX_POINT_LIGHTS = 4;
const int MAX_SPOT_LIGHTS = 4;

in vec2 texCoord0;
in vec3 normal0;
in vec3 worldPos0;
out vec4 fagColor;

uniform vec3 baseColor;
uniform vec3 ambientLight;
uniform sampler2D sampler;

uniform vec3 eyePos;
uniform float specularIntensity;
uniform float specularPower;

struct BaseLight {
	vec3 color;
	float intensity;
};

struct DirectionalLight {
	BaseLight base;
	vec3 direction;
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

uniform DirectionalLight directionalLight;
uniform PointLight pointLights[MAX_POINT_LIGHTS];
uniform SpotLight spotLights[MAX_SPOT_LIGHTS];

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

vec4 calcDirectionalLight(DirectionalLight directionalLight, vec3 normal) {
	return calcLight(directionalLight.base, directionalLight.direction, normal);
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
	vec4 totalLight = vec4(ambientLight, 1);
	vec4 color = vec4(baseColor, 1);
	
	vec4 textureColor = texture(sampler, texCoord0.xy);
	if (textureColor != vec4(0, 0, 0, 0)) {
		color *= textureColor;
	}
	
	vec3 normal = normalize(normal0);
	
	totalLight += calcDirectionalLight(directionalLight, normal);
	
	// please do not make MAX_POINT_LIGHTS uniform, since
	// opengl does not handle variable in loop well.
	for (int i = 0; i < MAX_POINT_LIGHTS; i++) {
		if (pointLights[i].base.intensity > 0) { // opengl sometimes will be executed irrespect of the test
			totalLight += calcPointLight(pointLights[i], normal);
		}
	}
	
	for (int i = 0; i < MAX_SPOT_LIGHTS; i++) {
		if (spotLights[i].pointLight.base.intensity > 0) { // opengl sometimes will be executed irrespect of the test
			totalLight += calcSpotLight(spotLights[i], normal);
		}
	}
	
	fagColor = color * totalLight;
}
