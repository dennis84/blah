import {h} from 'virtual-dom'
import debounce from 'debounce'
import * as widgets from './widgets'
import masonry from './masonry'
import nav from './nav'
import error from './common/error'

function render(update, conn, model) {
  return h('div.container', [
    nav(model, update, conn),
    h('h1.is-text-centered', 'User Stats'),
    error(model) || masonry({className: 'widgets', itemSelector: '.widget'}, [
      widgets.visitorsToday(model, update, conn),
      widgets.visitorsByCountry(model, update, conn),
      widgets.uniqueVisitors(model, update, conn)
    ])
  ])
}

export default render
