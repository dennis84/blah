import clone from 'clone'

function add(model, value) {
  var m = clone(model)
  var highlighted = model.options.find(x => x.highlighted)

  if(highlighted) {
    var index = model.options.indexOf(highlighted)
    m.options[index].highlighted = false
    m.options[index].selected = true
    m.options[index].hidden = true
    m.values.push(highlighted.value)
    return m
  }

  if('' === value) return m

  var option = model.options.find(x => x.value === value)

  if(option) {
    var index = model.options.indexOf(option)
    m.options[index].selected = true
    m.options[index].hidden = true
  }

  m.values.push(value)
  return m
}

function pop(model) {
  var m = clone(model)
  var value = m.values.pop()
  var option = model.options.find(x => x.value === value)

  if(option) {
    var index = model.options.indexOf(option)
    m.options[index].selected = false
    m.options[index].hidden = false
  }

  return m
}

function move(model, direction) {
  var m = clone(model)
  var next = null
  var highlightNext = false

  function fn(i) {
    var opt = model.options[i]
    if(!next && !opt.selected && !opt.hidden && !opt.highlighted) next = opt
    if(highlightNext && !opt.selected && !opt.hidden) {
      highlightNext = false
      next = opt 
    }

    if(opt.highlighted) highlightNext = true
    m.options[i].highlighted = false
  }

  if('up' === direction) {
    for(var i = model.options.length - 1; i >= 0; i--) fn(i)
  } else {
    for(var i = 0; i < model.options.length; i++) fn(i)
  }

  if(next) {
    var index = model.options.indexOf(next)
    m.options[index].highlighted = true
  }

  return m
}

function filter(model, value) {
  var m = clone(model)
  for(var i in model.options) {
    var opt = model.options[i]

    if(!opt.selected && '' === value) {
      m.options[i].hidden = false
    } else if(!opt.selected && 0 === opt.value.indexOf(value)) {
      m.options[i].hidden = false
    } else {
      m.options[i].hidden = true
    }
  }

  return m
}

function open(model, value = true) {
  var m = clone(model)
  m.open = value
  return m
}

export {
  open,
  filter,
  move,
  pop,
  add
}
