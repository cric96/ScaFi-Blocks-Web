package it.unibo.scafi.compiler
//old way, reload the entire page
object ScalaCompiledPage {
  def html(scriptId: String): String =
    s"""<!DOCTYPE html>
      |<html>
      |<head>
      |    <meta charset="UTF-8">
      |    <title>Scafi</title>
      |</head>
      |<body>
      |    <script src="https://cdnjs.cloudflare.com/ajax/libs/blockly/8.0.0/blockly_compressed.min.js"></script>
      |    <script id="common" type="text/javascript" src="/js/common.js"></script>
      |    <script id="scafiWebReload" type="text/javascript" src="/js/$scriptId"></script>
      |    <script type="text/javascript" src="js/scafi-blocks-utils.js"></script>
      |    <script type="text/javascript" src="js/scafi-blocks-opt.js"></script>
      |    <script>
      |    loadWorkspace();
      |    setTimeout(() => { window.dispatchEvent(new Event('resize')); }, 100);
      |    </script>
      |    <script>
      |    // hide code selection
      |    document.getElementById('code-section').style.display = 'none';
      |    // when show-code is clicked, show the code if it is hidden, otherwise hide it and show the blockly editor with editor-section id
      |    document.getElementById('show-code').addEventListener('click', function() {
      |        if (document.getElementById('code-section').style.display === 'none') {
      |            document.getElementById('code-section').style.display = 'block';
      |            document.getElementById('blockly-editor').style.display = 'none';
      |            codeSection.setValue(ScafiBlocks)
      |        } else {
      |            document.getElementById('code-section').style.display = 'none';
      |            document.getElementById('blockly-editor').style.display = 'block';
      |        }
      |    });
      |    </script>
      |</body>
      |<style>
      |#blockly-editor {
      |    height: 80vh;
      |    width: 100%;
      |}
      |</style>
      |</html>""".stripMargin
}
