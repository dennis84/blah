function path(model, path) {
  model.path = path
  return model
}

function toggleNav(model) {
  model.isNavVisble = !model.isNavVisble
  return model
}

function toggleDropdown(model) {
  model.isDropdownVisble = !model.isDropdownVisble
  return model
}

function noop(model) {
  return model
}

module.exports = {
  path: path,
  toggleNav: toggleNav,
  toggleDropdown: toggleDropdown,
  noop: noop,
}
