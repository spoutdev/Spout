# Valid shader descriptor.  Renderer will select the shader version depending on which mode the client is started in
Shader: shader://Spout/shaders/postprocess/ssao.ssf

RenderState:
    # Whether or not to use the Depth Buffer for rendering this material.  Defaults to True.
    Depth: true
    # View and projection matricies.  Used to override the rendering mode to a static matrix.
    # null matrix uses Main Camera
    # Defaults to null.
    Projection: null
    View: null
