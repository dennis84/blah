import main from '../../main'
import bindAppend from '../../bind-append'
import render from './render'

class Multiselect {
  constructor(options = {}) {
    this.type = 'Widget'
    this.options = options
  }

  init() {
    var options = []
    var values = this.options.values || []
    if(undefined !== this.options.options) {
      options = this.options.options.map(x => {
        var selected = -1 !== values.indexOf(x)
        return {
          value: x,
          selected: selected,
          hidden: selected,
          highlighted: false
        }
      })
    }

    var model = {
      options: options,
      values: values
    }

    var elem = document.createElement('div')
    var renderFn = bindAppend(render, null, update)
    var loop = main(model, renderFn, elem)

    function update(fn, ...args) {
      loop.update(fn(model, ...args))
      model = loop.state
    }

    return elem
  }

  update() {
  }

  destroy() {
  }
}

export default Multiselect
