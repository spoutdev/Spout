# Valid shader descriptor.  Renderer will select the shader version depending on which mode the client is started in
Shader: shader://Spout/resources/resources/shaders/basic.ssf

RenderState:
    # Whether or not to use the Depth Buffer for rendering this material.  Defaults to True.
    Depth: true
    # View and projection matricies.  Used to override the rendering mode to a static matrix.
    # null matrix uses Main Camera
    # Defaults to null.
    Projection: null
    View: null

# Parameters for the Shader.
# Accepted Types:
# - int
# - float/double
# - Vectors
#   - vec2(0,0)
#   - vec3(0,0,0)
#   - vec4(0,0,0,0)
# - Colors
#   -RGB: color(0,0,0)
#   -RGBA: color(0,0,0,0)
# - Matrix (***NOT IMPLEMENTED***)(Only square matricies supported)
#   - mat2(0,0,0,0)
#   - mat3(0,0,0, 0,0,0, 0,0,0)
#   - mat4(0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0)
# - Textures
#   - texture://PLUGIN/path/to/texture
#   - supported extensions: gif, png, jpg, bmp

# Names match up to the name in the shader.

MaterialParams:
    Diffuse: texture://Spout/resources/fallbacks/fallback.png
    BlendColor: color(1,1,1,1)
