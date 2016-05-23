use hdfs::channel::Channel;

pub trait Client {
    fn mkdir(&mut self);
}

impl Client for Channel {
    fn mkdir(&mut self) {
        self.send();
    }
}
