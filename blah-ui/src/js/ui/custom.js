import {h} from 'virtual-dom'
import masonry from './masonry'
import nav from './nav'
import widget from '../widget'
import count from './count/count'
import countDiff from './count/count-diff'
import bar from './count/bar'
import pie from './count/pie'
import userCount from './user/count'
import userBar from './user/bar'
import sum from './sum/sum'
import funnel from './funnel/funnel'

var widgetFns = {
  count: count,
  diff: countDiff,
  bar: bar,
  pie: pie,
  userCount: userCount,
  userBar: userBar,
  sum: sum,
  funnel: funnel
}

function render(update, conn, model) {
  var widgets = window.localStorage.getItem('widgets')
  if(widgets) widgets = JSON.parse(widgets)
  else widgets = []

  return h('div.container', [
    nav(model, update, conn),
    h('h1.center-hv', 'Custom'),
    masonry({
      className: 'widgets',
      itemSelector: '.widget'
    }, widgets.map((data) => {
      return widget(widgetFns[data.type], model, update, {}, conn, data)
    }))
  ])
}

export default render
