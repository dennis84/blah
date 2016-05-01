import {h} from 'virtual-dom'
import {hook} from '../hook'

function theme(model) {
  return hook(node => {
    var elem = node
    while(elem = elem.parentNode) {
      if(elem.matches('html')) {
        if('dark' === model.theme) {
          elem.classList.add('dark')
        } else {
          elem.classList.remove('dark')
        }

        return
      }
    }
  })
}

export default theme
