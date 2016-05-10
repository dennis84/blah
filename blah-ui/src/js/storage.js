class Storage {
  constructor(storage, ttl) {
    this.storage = storage
    this.ttl = undefined === ttl ? 1209600000 : ttl
  }

  set(key, data) {
    data.timestamp = Date.now()
    this.storage.setItem(key, JSON.stringify(data))
  }

  get(key, def=null) {
    var data = this.storage.getItem(key)
    if(!data) return def
    data = JSON.parse(data)
    if(data.timestamp + this.ttl < Date.now()) {
      this.storage.removeItem(key)
      return def
    }
    delete data['timestamp']
    return data
  }
}

export default Storage
