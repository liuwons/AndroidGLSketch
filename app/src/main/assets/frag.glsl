 precision mediump float;

 uniform float u_AxisScale;
 uniform float u_WindowWidth;
 uniform float u_WindowHeight;
 uniform float u_BorderWidth;

 varying float fShapeType;
 varying vec4 fColor;
 varying vec4 fPosition;
 varying float fLineWidth;

 float pointDistToLine(vec2 p1, vec2 p2, vec2 point)
 {
     vec2 vecLine = p2 - p1;
     vec2 normal = vec2(vecLine.y, -vecLine.x);
     vec2 dir = p1 - point;
     return abs(dot(normalize(normal), dir));
 }

 void main() {
     if (fShapeType < 1000.0) {
         // bezier
         gl_FragColor = fColor;
     } else if (fShapeType < 1100.0) {
         // line
         vec2 start = fPosition.xy;
         vec2 end = fPosition.zw;

         float x = gl_FragCoord.x * u_AxisScale - (u_WindowWidth * u_AxisScale / 2.0);
         float y = gl_FragCoord.y * u_AxisScale - (u_WindowHeight * u_AxisScale / 2.0);

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
         gl_FragColor = fColor;
     } else if (fShapeType < 1300.0) {
         // arrow
         gl_FragColor = fColor;
     } else if (fShapeType < 1400.0) {
         // round
         gl_FragColor = fColor;
     }
 }