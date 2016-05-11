function bindAppend(fn, context, ...xs) {
  return (...ys) => fn.apply(context, ys.concat(xs))
}

export default bindAppend
