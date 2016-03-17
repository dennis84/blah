import {h} from 'virtual-dom'
import * as samples from './samples'
import masonry from './masonry'
import nav from './nav'

function render(update, conn, model) {
  return h('div.container', [
    nav(model, update, conn),
    h('h1.center-hv', 'Misc'),
    masonry({className: 'widgets', itemSelector: '.widget'}, [
      samples.itemsFunnel(model, update, conn),
      samples.totalRevenue(model, update, conn),
      samples.recommendationsWidget(model, update, conn)
    ])
  ])
}

export default render
