<%@ attribute name="oca" required="false" rtexprvalue="true" type="org.springframework.samples.parchisoca.game.Oca"
 description="Oca to be rendered" %>
<canvas id="canvas" width="${oca.width}" height="${oca.height}"></canvas>
<img id="source" src="${oca.background}" style="display:none">

<img id="piece_BLUE" src="/resources/images/piece_BLUE.png" style="display:none">
<img id="piece_YELLOW" src="/resources/images/piece_YELLOW.png" style="display:none">
<img id="piece_GREEN" src="/resources/images/piece_GREEN.png" style="display:none">
<img id="piece_red" src="/resources/images/piece_RED.png" style="display:none">

<script>
var canvas = document.getElementById("canvas");
var ctx = canvas.getContext("2d");
var image = document.getElementById('source');

ctx.drawImage(image, 0, 0, ${oca.width}, ${oca.height});
</script>