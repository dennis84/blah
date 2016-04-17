import {h} from 'virtual-dom'
import nav from './nav'
import widget from '../widget'
import explore from './people/explore'
import * as error from './common/error'

function render(update, conn, model) {
  return h('div.container', [
    nav(model, update, conn),
    h('h1.is-text-centered', 'People'),
    (model.error) ? error.unknown() : widget(explore, model, update)
  ])
}

export default render
