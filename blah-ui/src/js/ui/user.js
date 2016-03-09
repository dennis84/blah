import {h} from 'virtual-dom'
import debounce from 'debounce'
import * as widgets from './widgets'
import masonry from './masonry'
import nav from './nav'

function render(model, chan, conn) {
  return h('div.container', [
    nav(model, chan, conn),
    h('h1.center-hv', 'User Stats'),
    masonry({className: 'widgets', itemSelector: '.widget'}, [
      widgets.visitorsToday(model, chan, conn),
      widgets.visitorsByCountry(model, chan, conn),
      widgets.uniqueVisitors(model, chan, conn)
    ])
  ])
}

export default render
