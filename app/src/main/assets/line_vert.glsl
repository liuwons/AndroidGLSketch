uniform mat4 u_Matrix;

attribute vec4 vPosition;
attribute vec4 vColor;
attribute float vLineWidth;
attribute float vPointID;  // 0.1, 1.1, 2.1, 3.1

varying vec4 fColor;

void main() {
    fColor = vColor;

    vec2 start = vPosition.xy;
    vec2 end = vPosition.zw;

    vec2 v = vec2(start.y - end.y, end.x - start.x);

    vec2 normal = normalize(v);

    float x = 0.0;
    float y = 0.0;

    float scaledOffsetX = vLineWidth / 2.0 * normal.x;
    float scaledOffsetY = vLineWidth / 2.0  * normal.y;
    if (vPointID < 1.0) {
        x = start.x + scaledOffsetX;
        y = start.y + scaledOffsetY;
    } else if(vPointID < 2.0) {
        x = start.x - scaledOffsetX;
        y = start.y - scaledOffsetY;
    } else if (vPointID < 3.0) {
        x = end.x + scaledOffsetX;
        y = end.y + scaledOffsetY;
    } else {
        x = end.x - scaledOffsetX;
        y = end.y - scaledOffsetY;
    }

    gl_Position = u_Matrix * vec4(x, y, 0, 1.0);
}