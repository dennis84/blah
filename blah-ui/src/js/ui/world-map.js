import {h} from 'virtual-dom'
import nav from './nav'
import widget from '../widget'
import world from './map/world'
import * as error from './common/error'

function render(update, conn, model) {
  return h('div.container', [
    nav(model, update, conn),
    h('h1.is-text-centered', 'World Map'),
    (model.error) ? error.unknown() : widget(world, model, update, {}, conn)
  ])
}

export default render
