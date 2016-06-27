import {h} from 'virtual-dom'
import debounce from 'debounce'
import {hook, mount} from '../../hook'
import {open, toggle, pop, next, prev, highlight, filter} from './ctrl'

const KEY_UP = 38
const KEY_DOWN = 40
const KEY_BACKSPACE = 8
const KEY_ENTER = 13
const KEY_ESC = 27

function menu(model, update) {
  return h('div.multiselect-menu', {
    className: model.open ? '' : 'is-hidden'
  }, model.options.map(x => {
    if(x.hidden) return
    var highlighted = x.highlighted ? 'is-highlighted' : ''
    var selected = x.selected ? 'is-selected' : ''
    return h('div.multiselect-option', {
      className: `${highlighted} ${selected}`,
      onmouseover: (e) => update(highlight, x),
      onmousedown: (e) => {
        e.target.parentNode.parentNode.parentNode
          .querySelector('.multiselect-input')
          .blurDisabled = true
        update(toggle, x.value)
      }
    }, x.value)
  }))
}

function input(model, update) {
  return h('input.multiselect-input', {
    onfocus: (e) => update(open),
    onblur: (e) => {
      e.preventDefault()
      if(model.multiple && e.target.blurDisabled) {
        e.target.blurDisabled = false
        e.target.focus()
        return
      }

      update(open, false)
    },
    onkeydown: e => {
      var value = e.target.value.trim()
      if(KEY_BACKSPACE === e.keyCode && '' === value) {
        update(pop)
      }
    },
    onkeyup: e => {
      var value = e.target.value.trim()
      if(KEY_ENTER === e.keyCode) {
        update(toggle, value)
        update(filter, '')
        e.target.value = ''
        if(!model.multiple) {
          update(open, false)
          e.target.blur()
        }
      } else if(KEY_UP === e.keyCode) {
        if(!model.open) update(open)
        update(prev)
      } else if(KEY_DOWN === e.keyCode) {
        if(!model.open) update(open)
        update(next)
      } else if(KEY_ESC === e.keyCode) {
        update(open, false)
        e.target.blur()
      } else {
        update(filter, value)
      }
    }
  })
}

function value(model, update, x) {
  return h('div.multiselect-value', x)
}

function render(model, update) {
  return h('div.multiselect', {
    className: model.multiple ? 'is-multiple' : ''
  }, [
    h('div.multiselect-control', {
      tabIndex: 0,
      onfocus: (e) => e.target.querySelector('.multiselect-input').focus(),
    }, [
      h('div.multiselect-values', model.values.map(x =>
        value(model, update, x)
      )),
      input(model, update)
    ]),
    menu(model, update)
  ])
}

export default render
