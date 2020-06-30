uniform mat4 u_Matrix;
uniform float u_BorderWidth;

attribute vec4 vPosition;
attribute vec4 vColor;
attribute float vLineWidth;
attribute float vPointIndicator;  // line: 0.1, 1.1, 2.1, 3.1;  bezier: t val
attribute float vZ;
attribute vec4 vCtrl;

varying float fShapeType;
varying vec4 fColor;
varying vec4 fPosition;
varying float fLineWidth;

vec2 b3(in vec2 p0, in vec2 p1, in vec2 p2, in vec2 p3, in float t)
{
    vec2 q0 = mix(p0, p1, t);
    vec2 q1 = mix(p1, p2, t);
    vec2 q2 = mix(p2, p3, t);

    vec2 r0 = mix(q0, q1, t);
    vec2 r1 = mix(q1, q2, t);

    return mix(r0, r1, t);
}

void main() {
    fShapeType = vCtrl.x;
    if (fShapeType < 1000.0) {
        // bezier
        vec4 pos;
        pos.w = 1.0;

        vec2 p0 = vPosition.xy;
        vec2 p3 = vPosition.zw;

        vec2 p1 = vCtrl.xy;
        vec2 p2 = vCtrl.zw;

        float t = abs(vPointIndicator);

        float d = 0.01;
        float neighborT;

        vec2 selfPos = vec2(0.0, 0.0);
        bool isEnd = false;
        if (t > 1000.0) {
            // end vertex
            selfPos = vPosition.zw;
            neighborT = 1.0 - d;
            isEnd = true;
        } else if (t < 0.0001) {
            // start vertex
            selfPos = vPosition.xy;
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
        float sign = vPointIndicator / t;
        pos.xy = selfPos + normal / normalLen * vLineWidth / 2.0 * sign;

        fColor = vColor;

        vec4 p = u_Matrix * pos;
        gl_Position =  vec4(p.x, p.y, vZ, 1.0);
    } else if (fShapeType < 10000.0) {
        // line
        fColor = vColor;
        fPosition = vPosition;
        fLineWidth = vLineWidth;

        vec2 start = vPosition.xy;
        vec2 end = vPosition.zw;

        vec2 v = vec2(start.y - end.y, end.x - start.x);

        vec2 normal = normalize(v);

        float x = 0.0;
        float y = 0.0;

        float scaledOffsetX = vLineWidth / 2.0 * normal.x;
        float scaledOffsetY = vLineWidth / 2.0  * normal.y;
        if (vPointIndicator < 1.0) {
            x = start.x + scaledOffsetX;
            y = start.y + scaledOffsetY;
        } else if(vPointIndicator < 2.0) {
            x = start.x - scaledOffsetX;
            y = start.y - scaledOffsetY;
        } else if (vPointIndicator < 3.0) {
            x = end.x + scaledOffsetX;
            y = end.y + scaledOffsetY;
        } else {
            x = end.x - scaledOffsetX;
            y = end.y - scaledOffsetY;
        }

        vec4 p = u_Matrix * vec4(x, y, vZ, 1.0);
        gl_Position = vec4(p.x, p.y, vZ, 1.0);
    } else if (fShapeType < 100000.0) {
        // oval
    }

}