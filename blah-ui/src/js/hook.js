class Hook {
  constructor(fn) {
    this.fn = fn
  }

  hook(node, propName, prevValue) {
    this.fn.apply(this, [node])
  }
}

var elems = new Set
if(typeof window !== 'undefined') {
  window.addEventListener('hashchange', () => elems.clear())
}

class MountHook extends Hook {
  hook(node, propName, prev) {
    if(!elems.has(node)) {
      elems.add(node)
      this.fn.apply(this, [node])
    }
  }
}

function hook(fn) {
  return new Hook(fn)
}

function mount(fn) {
  return new MountHook(fn)
}

export {
  hook,
  mount
}
