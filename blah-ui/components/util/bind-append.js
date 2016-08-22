function bindAppend(fn, context) {
  var xs = [].slice.call(arguments).slice(2)
  return function() {
    return fn.apply(context, [].slice.call(arguments).concat(xs))
  }
}

module.exports = bindAppend
