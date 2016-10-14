var snabbdom = require('snabbdom')
var patch = snabbdom.init([
  require('snabbdom/modules/class'),
  require('snabbdom/modules/props'),
  require('snabbdom/modules/style'),
  require('snabbdom/modules/eventlisteners'),
])
var render = require('./render')
var ctrl = require('./ctrl')
var chart = require('./chart')

var apps = [
  {app: 'Foo', cpu_usage: 10, mem_usage: 4, instances: 1, max_instances: 1},
  {app: 'Bar', cpu_usage: 20, mem_usage: 20, instances: 2, max_instances: 2},
  {app: 'Bar', cpu_usage: 12, mem_usage: 18, instances: 2, max_instances: 2},
  {app: 'Baz', cpu_usage: 30, mem_usage: 12, instances: 1, max_instances: 10},
  {app: 'Qux', cpu_usage: 40, mem_usage: 82, instances: 5, max_instances: 10},
  {app: 'Qux', cpu_usage: 20, mem_usage: 12, instances: 5, max_instances: 10},
  {app: 'Qux', cpu_usage: 80, mem_usage: 83, instances: 5, max_instances: 10},
  {app: 'Qux', cpu_usage: 30, mem_usage: 65, instances: 5, max_instances: 10},
  {app: 'Qux', cpu_usage: 90, mem_usage: 34, instances: 5, max_instances: 10},
  {app: 'Quux', cpu_usage: 50, mem_usage: 46, instances: 2, max_instances: 10},
  {app: 'Quux', cpu_usage: 40, mem_usage: 32, instances: 2, max_instances: 10},
  {app: 'Corge', cpu_usage: 60, mem_usage: 98, instances: 8, max_instances: 10},
  {app: 'Corge', cpu_usage: 70, mem_usage: 32, instances: 8, max_instances: 10},
  {app: 'Corge', cpu_usage: 20, mem_usage: 54, instances: 8, max_instances: 10},
  {app: 'Corge', cpu_usage: 10, mem_usage: 12, instances: 8, max_instances: 10},
  {app: 'Corge', cpu_usage: 3, mem_usage: 45, instances: 8, max_instances: 10},
  {app: 'Corge', cpu_usage: 6, mem_usage: 76, instances: 8, max_instances: 10},
  {app: 'Corge', cpu_usage: 8, mem_usage: 12, instances: 8, max_instances: 10},
  {app: 'Corge', cpu_usage: 43, mem_usage: 65, instances: 8, max_instances: 10}
]

function Autoscale(node, options) {
  var state = {apps: []}
  var vnode = render(state, update, options)

  function update(fn) {
    var args = [].slice.call(arguments, 1)
    var res = fn.apply(null, [state].concat(args))
    state = res
    vnode = patch(vnode, render(res, update, options))
  }

  var source = new EventSource("http://localhost:8003");
  source.onmessage = function(e) {
    update(ctrl.update, JSON.parse(e.data))
  }

  patch(node, vnode)
  update(ctrl.update, apps)
}

module.exports = Autoscale
