import {h} from 'virtual-dom'
import * as widgets from './widgets'
import masonry from './masonry'
import nav from './nav'

function render(update, conn, model) {
  return h('div.container', [
    nav(model, update, conn),
    h('h1.center-hv', 'Misc'),
    masonry({className: 'widgets', itemSelector: '.widget'}, [
      widgets.itemsFunnel(model, update, conn),
      widgets.totalRevenue(model, update, conn),
      widgets.recommendationsWidget(model, update, conn)
    ])
  ])
}

export default render
