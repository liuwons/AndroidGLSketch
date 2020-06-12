uniform mat4 u_Matrix;

attribute vec4 a_Color;
attribute vec4 a_BzPos;
attribute vec4 a_BzCtrl;
attribute float a_TData;// Bezier t variable, 0.00001(start), 10000(end)
attribute float a_LineWidth;

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

    vec2 p0 = a_BzPos.xy;
    vec2 p3 = a_BzPos.zw;

    vec2 p1 = a_BzCtrl.xy;
    vec2 p2 = a_BzCtrl.zw;

    float t = abs(a_TData);

    float d = 0.01;
    float neighborT;

    vec2 selfPos = vec2(0.0, 0.0);
    bool isEnd = false;
    if (t > 1000.0) {
        // end vertex
        selfPos = a_BzPos.zw;
        neighborT = 1.0 - d;
        isEnd = true;
    } else if (t < 0.0001) {
        // start vertex
        selfPos = a_BzPos.xy;
        neighborT = d;
    } else {
        // normal
        selfPos = b3(p0, p1, p2, p3, t);
        neighborT = t + d;
    }

    vec2 neighborPos = b3(p0, p1, p2, p3, neighborT);
    vec2 direction = vec2(0.0, 0.0);
    if (isEnd) {
        direction = selfPos - neighborPos;
    } else {
        direction = neighborPos - selfPos;
    }

    vec2 normal = vec2(-direction.y, direction.x);
    float normalLen = pow(normal.x * normal.x + normal.y * normal.y, 0.5);
    float sign = a_TData / t;
    pos.xy = selfPos + normal / normalLen * a_LineWidth / 2.0 * sign;

    v_Color = a_Color;

    gl_Position =  u_Matrix * pos;
    gl_PointSize = 2.0;
}
