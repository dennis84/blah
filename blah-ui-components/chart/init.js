function init(render) {
  return function(node) {
    var args = arguments
    setTimeout(function draw() {
      node.innerHTML = ''
      render.apply(null, args)
      window.addEventListener('resize', draw)
    }, 0)
  }
}

module.exports = init
