import {h} from 'virtual-dom'
import {hook} from '../hook'

function theme(model) {
  return hook(node => {
    var html = document.documentElement
    html.className = ''
    html.classList.add(model.theme)
  })
}

export default theme
