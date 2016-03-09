import {h} from 'virtual-dom'
import * as widgets from './widgets'
import masonry from './masonry'
import nav from './nav'

function render(model, chan, conn) {
  return h('div.container', [
    nav(model, chan, conn),
    h('h1.center-hv', 'Misc'),
    masonry({className: 'widgets', itemSelector: '.widget'}, [
      widgets.itemsFunnel(model, chan, conn),
      widgets.totalRevenue(model, chan, conn),
      widgets.recommendationsWidget(model, chan, conn)
    ])
  ])
}

export default render
