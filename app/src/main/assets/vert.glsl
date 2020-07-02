uniform mat4 u_Matrix;
uniform float u_BorderWidth;

attribute vec4 vPosition;
attribute vec4 vColor;
attribute float vLineWidth;
attribute float vPointIndicator;  // line: 0.1, 1.1, 2.1, 3.1;  bezier: t val; oval: angle
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
    } else if (fShapeType < 1100.0) {
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
    } else if (fShapeType < 1200.0) {
        // oval
        fColor = vColor;
        fPosition = vPosition;
        fLineWidth = vLineWidth;

        vec2 center = vec2((vPosition.x + vPosition.z) / 2.0, (vPosition.y + vPosition.w) / 2.0);
        float a = abs(vPosition.x - vPosition.z) / 2.0;
        float b = abs(vPosition.y - vPosition.w) / 2.0;

        float pi = 3.14159;
        float angle = 2.0 * pi * abs(vPointIndicator);

        float x = a * cos(angle);
        float y = b * sin(angle);

        float deltaAngle = 0.001;
        vec2 v = vec2(b * sin(angle + deltaAngle) - y, x - a * cos(angle + deltaAngle));
        vec2 normal = normalize(v);
        float scaledOffsetX = vLineWidth / 2.0 * normal.x;
        float scaledOffsetY = vLineWidth / 2.0 * normal.y;

        if (vCtrl.y > 0.0) {
            x += scaledOffsetX;
            y += scaledOffsetY;
        } else {
            x -= scaledOffsetX;
            y -= scaledOffsetY;
        }

        x += center.x;
        y += center.y;

        vec4 p = u_Matrix * vec4(x, y, vZ, 1.0);
        gl_Position = vec4(p.x, p.y, vZ, 1.0);
    } else if (fShapeType < 1300.0) {
        // arrow
        fColor = vColor;
        fPosition = vPosition;
        fLineWidth = vLineWidth;

        vec2 start = vPosition.xy;
        vec2 end = vPosition.zw;

        float ovalSliceCount = vCtrl.w;

        if (vPointIndicator < 0.0) {
            // 圆心
            vec4 p = u_Matrix * vec4(start.x, start.y, 0.0, 1.0);
            gl_Position = vec4(p.x, p.y, vZ, 1.0);
        } else if (vPointIndicator < 2.0) {
            // 圆周
            float pi = 3.14159;
            float angle = 2.0 * pi * abs(vPointIndicator);

            float radius = vLineWidth / 7.0 / 2.0;
            float x = start.x + radius * cos(angle);
            float y = start.y + radius * sin(angle);
            vec4 p = u_Matrix * vec4(x, y, 0.0, 1.0);
            gl_Position = vec4(p.x, p.y, vZ, 1.0);
        } else {
            highp int idx = int(vPointIndicator - ovalSliceCount - 1.0);

            vec2 v = vec2(start.y - end.y, end.x - start.x);
            vec2 normal = normalize(v);

            vec2 dir = start - end;
            vec2 offset = normalize(dir) * vLineWidth;

            if (idx < 4) {
                // 箭头体
                float startWidth = vLineWidth / 7.0;
                float endWidth = vLineWidth / 7.0 * 3.0;

                float x = start.x;
                float y = start.y;
                if (idx == 0) {
                    x += normal.x * startWidth / 2.0;
                    y += normal.y * startWidth / 2.0;
                } else if (idx == 1) {
                    x -= normal.x * startWidth / 2.0;
                    y -= normal.y * startWidth / 2.0;
                } else if (idx == 2) {
                    x = end.x + normal.x * endWidth / 2.0 + offset.x;
                    y = end.y + normal.y * endWidth / 2.0 + offset.y;
                } else if (idx == 3) {
                    x = end.x - normal.x * endWidth / 2.0 + offset.x;
                    y = end.y - normal.y * endWidth / 2.0 + offset.y;
                }
                vec4 p = u_Matrix * vec4(x, y, 0.0, 1.0);
                gl_Position = vec4(p.x, p.y, vZ, 1.0);
            } else {
                // 箭头
                float x = end.x;
                float y = end.y;
                if (idx != 6) {
                    x += offset.x;
                    y += offset.y;
                }
                if (idx == 4) {
                    x += normal.x * vLineWidth / 2.0;
                    y += normal.y * vLineWidth / 2.0;
                } else if (idx == 5) {
                    x -= normal.x * vLineWidth / 2.0;
                    y -= normal.y * vLineWidth / 2.0;
                }
                vec4 p = u_Matrix * vec4(x, y, 0.0, 1.0);
                gl_Position = vec4(p.x, p.y, vZ, 1.0);
            }
        }
    }

}