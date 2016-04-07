function init(render) {
  return (node, ...args) => {
    setTimeout(function draw() {
      node.innerHTML = ''
      render(node, ...args)
      window.addEventListener('resize', draw)
    }, 0)
  }
}

export default init
