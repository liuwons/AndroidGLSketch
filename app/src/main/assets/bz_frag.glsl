precision mediump float;       	// Set the default precision to medium. We don't need as high of a precision in the fragment shader.

varying vec4 v_Color;

void main()
{
    gl_FragColor = v_Color;
}
