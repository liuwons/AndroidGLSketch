uniform vec4 u_Color;
uniform vec4 u_BzData;
uniform vec4 u_BzDataCtrl;


attribute float a_TData; // Bezier t variable

varying vec4 v_Color; // wave color

vec2 b3( in vec2 p0, in vec2 p1, in vec2 p2, in vec2 p3, in float t )
{
	float tt = (1.0 - t) * (1.0 - t);

    return tt * (1.0 - t) * p0 +
        3.0 * t * tt * p1 +
                 3.0 * t * t * (1.0 - t) * p2 +
                 t * t * t * p3;
}

vec2 b3_( in vec2 p0, in vec2 p1, in vec2 p2, in vec2 p3, in float t )
{
    vec2 q0 = mix(p0, p1, t);
    vec2 q1 = mix(p1, p2, t);
    vec2 q2 = mix(p2, p3, t);

    vec2 r0 = mix(q0, q1, t);
    vec2 r1 = mix(q1, q2, t);

    return mix(r0, r1, t);
}

void main()
{
  vec4 pos;
  pos.w = 1.0;

  vec2 p0 = u_BzData.xy;
  vec2 p3 = u_BzData.zw;

  vec2 p1 = u_BzDataCtrl.xy;
  vec2 p2 = u_BzDataCtrl.zw;

  float t = a_TData;

  vec2 bPoint = b3_(p0, p1, p2, p3, t);

  if (t < -0.1)
  {
    //bottom points
    pos.xy = vec2(0.0, 0.0);
  }
  else
  {
    pos.xy = bPoint;
  }

  v_Color = u_Color;

  gl_Position = pos;
}
