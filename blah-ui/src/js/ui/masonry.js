var h = require('snabbdom/h').default
var Masonry = require('masonry-layout')
var debounce = require('debounce')

function render(options, items) {
  var masonryFn = debounce(function(vnode) {
    if(undefined === vnode.masonry) {
      vnode.masonry = new Masonry(vnode.elm, {
        itemSelector: options.itemSelector,
        transitionDuration: 0
      })
    } else {
      vnode.masonry.reloadItems()
      vnode.masonry.layout()
    }
  }, 100)

  return h('div', {
    class: options.class,
    hook: {
      insert: masonryFn,
      update: masonryFn
    }
  }, items)
}

module.exports = render
