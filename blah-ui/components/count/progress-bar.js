var h = require('snabbdom/h')

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
  var data = [
    {key: 'a', value: value},
    {key: 'b', value: 100 - value}
  ]

  return h('div.circle-progress-bar', {
    hook: {
      insert: function(vnode) {
        Chart.donut(vnode.elm, data, {donutWidth: 10})
      }
    }
  })
}

module.exports = progressBar
