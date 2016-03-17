import {h} from 'virtual-dom'
import debounce from 'debounce'
import * as samples from './samples'
import masonry from './masonry'
import nav from './nav'

function render(update, conn, model) {
  return h('div.container', [
    nav(model, update, conn),
    h('h1.center-hv', 'User Stats'),
    masonry({className: 'widgets', itemSelector: '.widget'}, [
      samples.visitorsToday(model, update, conn),
      samples.visitorsByCountry(model, update, conn),
      samples.uniqueVisitors(model, update, conn)
    ])
  ])
}

export default render
