class Hook {
  constructor(fn) {
    this.fn = fn
  }

  hook(node, propName, prevValue) {
    this.fn.apply(this, [node])
  }
}

class MountHook extends Hook {
  hook(node, propName, prevValue) {
    if(undefined !== prevValue) return
    this.fn.apply(this, [node])
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
