uniform vec4 u_Color;
uniform vec4 u_BzPos;
uniform vec4 u_BzCtrl;


attribute float a_TData;// Bezier t variable

varying vec4 v_Color;

vec2 b3(in vec2 p0, in vec2 p1, in vec2 p2, in vec2 p3, in float t)
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

    vec2 p0 = u_BzPos.xy;
    vec2 p3 = u_BzPos.zw;

    vec2 p1 = u_BzCtrl.xy;
    vec2 p2 = u_BzCtrl.zw;

    float t = a_TData;

    vec2 bPoint = b3(p0, p1, p2, p3, t);

    pos.xy = bPoint;

    v_Color = u_Color;

    gl_Position = pos;
    gl_PointSize = 10.0;
}
