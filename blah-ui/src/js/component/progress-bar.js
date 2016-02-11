import {h} from 'virtual-dom'
import {hook} from '../hook'
import Chartist from 'chartist'

/**
 * Returns a div with a pie chart.
 *
 * ```
 * <div class="chart"><svg></div>
 * ```
 *
 * @param {Int} value
 *
 * @return {VNode}
 */
function progressBar(value) {
  value = value > 100 ? 100 : value
  return h('div.circle-progress-bar', {
    hook: hook(node => {
      new Chartist.Pie(node, {
        series: [value, 100 - value]
      }, {donut: true, donutWidth: 10, showLabel: false})
    })
  })
}

export default progressBar
