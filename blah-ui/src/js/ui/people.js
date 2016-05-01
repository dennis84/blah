import {h} from 'virtual-dom'
import nav from './nav'
import widget from '../widget'
import explore from './people/explore'
import * as error from './common/error'
import theme from './theme'

function render(update, conn, model) {
  return h('div.container', {
    theme: theme(model)
  }, [
    nav(model, update, conn),
    h('h1.is-text-centered', 'People'),
    (model.error) ? error.unknown() : widget(explore, model, update)
  ])
}

export default render
