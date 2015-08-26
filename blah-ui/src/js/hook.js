class Hook {
  constructor(fn) {
    this.fn = fn
  }

  hook(...args) {
    this.fn.apply(this, args)
  }
}

function hook(fn) {
  return new Hook(fn)
}

export default hook
