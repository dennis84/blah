import {h} from 'virtual-dom'
import nav from './nav'
import widget from '../widget'
import explore from './people/explore'

function render(update, conn, model) {
  return h('div.container', [
    nav(model, update, conn),
    h('h1.center-hv', 'People'),
    widget(explore, model, update)
  ])
}

export default render
