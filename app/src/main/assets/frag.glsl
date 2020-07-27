 precision mediump float;

 uniform vec3 u_Transform;
 uniform float u_AxisScale;
 uniform float u_WindowWidth;
 uniform float u_WindowHeight;
 uniform float u_BorderWidth;

 varying float fShapeType;
 varying vec4 fColor;
 varying vec4 fPosition;
 varying float fLineWidth;
 varying float fPointIndex;

 float pointDistToLine(vec2 p1, vec2 p2, vec2 point)
 {
     vec2 vecLine = p2 - p1;
     vec2 normal = vec2(vecLine.y, -vecLine.x);
     vec2 dir = p1 - point;
     return abs(dot(normalize(normal), dir));
 }

 void main() {
     float transformX = u_Transform.x;
     float transformY = u_Transform.y;
     float scale = u_Transform.z;
     float x = (gl_FragCoord.x * u_AxisScale - (u_WindowWidth * u_AxisScale / 2.0)) / scale - transformX;
     float y = (gl_FragCoord.y * u_AxisScale - (u_WindowHeight * u_AxisScale / 2.0)) / scale - transformY;

     if (fShapeType < 1000.0) {
         // bezier
         gl_FragColor = fColor;

         vec2 pos = fPosition.xy;
         vec2 direction = fPosition.zw;

         float dist = pointDistToLine(pos, pos+direction, vec2(x, y));

         float solidRegionWidth = fLineWidth / 2.0 - u_BorderWidth;
         float alpha = 1.0;
         if (dist > solidRegionWidth) {
             alpha = 1.0 - (dist - solidRegionWidth) / u_BorderWidth;
         }
         if (alpha > 1.0) {
             alpha = 1.0;
         }
         if (alpha < 0.0) {
             alpha = 0.0;
         }

         gl_FragColor = vec4(fColor.r, fColor.g, fColor.b, alpha);

     } else if (fShapeType < 1100.0) {
         // line
         vec2 start = fPosition.xy;
         vec2 end = fPosition.zw;

         float dist = pointDistToLine(start, end, vec2(x, y));

         float solidRegionWidth = fLineWidth / 2.0 - u_BorderWidth;
         float alpha = 1.0;
         if (dist > solidRegionWidth) {
             alpha = 1.0 - (dist - solidRegionWidth) / u_BorderWidth;
         }
         if (alpha > 1.0) {
             alpha = 1.0;
         }
         if (alpha < 0.0) {
             alpha = 0.0;
         }

         gl_FragColor = vec4(fColor.r, fColor.g, fColor.b, alpha);
     } else if (fShapeType < 1200.0) {
         // oval
         vec2 center = vec2((fPosition.x + fPosition.z) / 2.0, (fPosition.y + fPosition.w) / 2.0);
         float a = abs(fPosition.x - fPosition.z) / 2.0;
         float b = abs(fPosition.y - fPosition.w) / 2.0;

         float pi = 3.14159;
         float angle = 2.0 * pi * abs(fPointIndex);

         vec2 cp = vec2(a * cos(angle), b * sin(angle));
         vec2 deltaP = vec2(0.0, 0.0);
         if ((fPointIndex > 1.0 / 8.0 && fPointIndex < 3.0 / 8.0) || (fPointIndex > 5.0 / 8.0 && fPointIndex < 7.0 / 8.0)) {
             deltaP.x = cp.x + 1.0;
             deltaP.y = cp.y - b / a * b / a * cp.x / cp.y;
         } else {
             deltaP.y = cp.y + 1.0;
             deltaP.x = cp.x - a / b * a / b * cp.y / cp.x;
         }

         x -= center.x;
         y -= center.y;

         float dist = pointDistToLine(cp, deltaP, vec2(x, y));

         float solidRegionWidth = fLineWidth / 2.0 - u_BorderWidth;
         float alpha = 1.0;
         if (dist > solidRegionWidth) {
             alpha = 1.0 - (dist - solidRegionWidth) / u_BorderWidth;
         }
         if (alpha > 1.0) {
             alpha = 1.0;
         }
         if (alpha < 0.0) {
             alpha = 0.0;
         }

         gl_FragColor = vec4(fColor.r, fColor.g, fColor.b, alpha);
     } else if (fShapeType < 1300.0) {
         // arrow
         vec2 start = fPosition.xy;
         vec2 end = fPosition.zw;

         vec2 v = vec2(x, y) - start;

         vec2 vecLine = normalize(end - start);
         vec2 normalLine = vec2(-vecLine.y, vecLine.x);

         float arrowBodyLength = length(end - start) - fLineWidth;

         float dst2Line = abs(dot(v, normalLine));
         float lengthOverLine = abs(dot(v, vecLine));

         float alpha = 1.0;
         if (lengthOverLine > arrowBodyLength) {
             // 箭头
             alpha = 1.0;
         } else {
             // 箭体
             float ratio = lengthOverLine / arrowBodyLength;
             if (ratio > 1.0) {
                 ratio = 1.0;
             }
             float startWidth = fLineWidth / 7.0;
             float endWidth = fLineWidth / 7.0 * 3.0;
             float width = mix(startWidth, endWidth, ratio);

             float solidRegionWidth = width / 2.0 - u_BorderWidth;
             if (dst2Line > solidRegionWidth) {
                 alpha = 1.0 - (dst2Line - solidRegionWidth) / u_BorderWidth;
             }
         }

         gl_FragColor = vec4(fColor.r, fColor.g, fColor.b, alpha);
     } else if (fShapeType < 1400.0) {
         // round
         vec2 center = vec2((fPosition.x + fPosition.z) / 2.0, (fPosition.y + fPosition.w) / 2.0);
         float radius = max(abs(fPosition.x - fPosition.z), abs(fPosition.y - fPosition.w)) / 2.0;

         vec2 pos = vec2(x, y);
         float dist = length(pos - center);

         float solidRegionWidth = radius - u_BorderWidth;
         float alpha = 1.0;
         if (dist > solidRegionWidth) {
             alpha = 1.0 - (dist - solidRegionWidth) / u_BorderWidth;
         }
         if (alpha > 1.0) {
             alpha = 1.0;
         }
         if (alpha < 0.0) {
             alpha = 0.0;
         }

         gl_FragColor = vec4(fColor.r, fColor.g, fColor.b, alpha);
     }
 }