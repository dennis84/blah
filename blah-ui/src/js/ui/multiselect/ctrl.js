import clone from 'clone'

function toggle(model, value) {
  var m = clone(model)
  var option = m.options.find(x => x.highlighted)
  if(!option && '' === value) return model

  if(option) {
    value = option.value
  } else {
    option = m.options.find(x => x.value === value)
  }

  if(!option) {
    option = {
      value: value,
      selected: false,
      highlighted: false,
      hidden: false
    }

    m.options.push(option)
  }

  var index = m.options.indexOf(option)

  if(option.selected) {
    m.options[index].highlighted = false
    m.options[index].selected = false
    var i = model.values.indexOf(option.value)
    m.values.splice(i, 1)
  } else {
    m.options[index].highlighted = false
    m.options[index].selected = true
    if(model.multiple) {
      m.values.push(value)
    } else {
      m = pop(m)
      m.values = [value]
    }
  }

  if(m.options[0]) m.options[0].highlighted = true

  return m
}

function pop(model) {
  var m = clone(model)
  var value = m.values.pop()
  var option = model.options.find(x => x.value === value)

  if(option) {
    var index = model.options.indexOf(option)
    m.options[index].highlighted = false
    m.options[index].selected = false
  }

  return m
}

function highlight(model, option) {
  var m = clone(model)
  for(var i in model.options) {
    var opt = model.options[i]
    if(opt === option) {
      m.options[i].highlighted = true
    } else {
      m.options[i].highlighted = false
    }
  }

  return m
}

function next(model) {
  var m = clone(model)
  var highlighted = m.options.find(x => x.highlighted)
  var index = m.options.indexOf(highlighted)
  var nextIndex = function findNext(i) {
    if(i >= m.options.length) i = 0
    var opt = m.options[i]
    if(!opt.hidden) return i
    findNext(i + 1)
  }(index + 1)

  m.options[index].highlighted = false
  m.options[nextIndex].highlighted = true
  return m
}

function prev(model) {
  var m = clone(model)
  var highlighted = m.options.find(x => x.highlighted)
  var index = m.options.indexOf(highlighted)
  var nextIndex = function findPrev(i) {
    if(i < 0) i = m.options.length - 1
    var opt = m.options[i]
    if(!opt.hidden) return i
    findPrev(i - 1)
  }(index - 1)

  m.options[index].highlighted = false
  m.options[nextIndex].highlighted = true
  return m
}

function filter(model, value) {
  var m = clone(model)
  for(var i in model.options) {
    var opt = model.options[i]

    if('' === value) {
      m.options[i].hidden = false
    } else if(0 === opt.value.indexOf(value)) {
      m.options[i].hidden = false
    } else {
      m.options[i].hidden = true
    }
  }

  return m
}

function open(model, value = true) {
  var m = clone(model)
  if(m.options[0]) m.options[0].highlighted = true
  m.open = value
  return m
}

export {
  open,
  filter,
  next,
  prev,
  highlight,
  pop,
  toggle
}
